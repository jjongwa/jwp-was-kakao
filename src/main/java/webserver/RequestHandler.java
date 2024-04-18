package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpMethod;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String CRLF = "\r\n";
    private static final String CREATE_USER_PATH = "/user/create";
    private static final String LOGIN_USER_PATH = "/user/login";
    private static final String LOCATION_INDEX_HTML = "Location: /index.html " + CRLF;
    private static final String LOCATION_LONGIN_FAILED_HTML = "Location: /user/login_failed.html " + CRLF;
    private static final String DEFAULT_PAGE_MESSAGE = "Hello, World!";
    private static final String NEW_CLIENT_CONNECT_MESSAGE = "New Client Connect! Connected IP : {}, Port : {}";
    private static final String USER_ID = "userId";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_LIST_PATH = "/user/list";
    private static final String LOCATION_USER_LOGIN_PATH = "Location: /user/login.html";
    private static final String LOGINED = "logined";
    private static final String TRUE = "true";
    private static final String USER_KEY = "user";
    private static final String CONTENT_TYPE = "Content-Type";

    private Socket connection;
    private SessionManager sessionManager = SessionManager.getInstance();

    public RequestHandler(final Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logConnectionDetails();
        try {
            processConnection();
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void logConnectionDetails() {
        logger.debug(NEW_CLIENT_CONNECT_MESSAGE, connection.getInetAddress(), connection.getPort());
    }

    private void processConnection() {
        try (
                final InputStream in = connection.getInputStream();
                final OutputStream out = connection.getOutputStream();
        ) {
            final HttpRequest httpRequest = HttpRequest.create(in);
            final HttpResponse httpResponse = HttpResponse.createEmptyResponse(out);
            if (httpRequest.isMethodEqual(HttpMethod.GET) && httpRequest.isPathEqual(USER_LIST_PATH)) {
                doGetUserList(httpRequest, httpResponse);
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.GET) &&
                httpRequest.isPathStartingWith(LOGIN_USER_PATH) &&
                sessionManager.findSession(httpRequest.getCookie().getValue(JSESSIONID)) != null
            ) {
                httpResponse.sendRedirect(LOCATION_INDEX_HTML);
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.GET)) {
                httpResponse.addHeader(CONTENT_TYPE, httpRequest.findContentType());
                httpResponse.forward(httpRequest.getPath());
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.POST) && httpRequest.isPathStartingWith(CREATE_USER_PATH)) {
                doPostCreateUser(httpRequest, httpResponse);
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.POST) && httpRequest.isPathStartingWith(LOGIN_USER_PATH)) {
                doPostLoginUser(httpRequest, httpResponse);
                return;
            }

            httpResponse.addHeader(CONTENT_TYPE, httpRequest.findContentType());
            httpResponse.forward(DEFAULT_PAGE_MESSAGE);
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void doPostCreateUser(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Map<String, String> queryParams = httpRequest.getBody();
        if (DataBase.isAlreadyExistId(queryParams.get(USER_ID))) {
            return;
        }
        DataBase.addUser(User.of(queryParams));
        httpResponse.sendRedirect(LOCATION_INDEX_HTML);
    }

    private void doPostLoginUser(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Map<String, String> queryParams = httpRequest.getBody();
        final HttpCookie cookieByRequest = httpRequest.getCookie();
        if (sessionManager.findSession(cookieByRequest.getValue(JSESSIONID)) != null) {
            httpResponse.sendRedirect(LOCATION_INDEX_HTML);
            return;
        }
        if (DataBase.isUserExist(queryParams.get(USER_ID), queryParams.get(PASSWORD))) {
            if (!cookieByRequest.containsKey(JSESSIONID)) {
                final UUID uuid = UUID.randomUUID();
                httpResponse.addHeader("Set-Cookie", JSESSIONID + "=" + uuid);
                makeSession(uuid.toString(), queryParams);
            }
            httpResponse.addHeader("Set-Cookie", LOGINED + "=" + TRUE);
            httpResponse.sendRedirect(LOCATION_INDEX_HTML);
            return;
        }
        httpResponse.sendRedirect(LOCATION_LONGIN_FAILED_HTML);
    }

    private void doGetUserList(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException, URISyntaxException {
        final HttpCookie cookieByRequest = httpRequest.getCookie();
        if (Objects.equals(cookieByRequest.getValue(LOGINED), TRUE)) {
            httpResponse.addHeader(CONTENT_TYPE, httpRequest.findContentType());
            httpResponse.forward(httpRequest.getPath());
            return;
        }
        httpResponse.sendRedirect(LOCATION_USER_LOGIN_PATH);
    }

    private void makeSession(final String uuid, final Map<String, String> queryParams) {
        if (sessionManager.findSession(uuid) == null) {
            sessionManager.add(uuid);
            final Session session = sessionManager.findSession(uuid);
            session.setAttribute(USER_KEY, DataBase.findUserById(queryParams.get(USER_ID)).get());
        }
    }
}
