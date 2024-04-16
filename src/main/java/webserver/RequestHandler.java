package webserver;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;
import webserver.request.HttpMethod;
import webserver.request.HttpRequest;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String CRLF = "\r\n";
    private static final String DEFAULT_FILE_NAME = "templates/index.html";
    private static final String CREATE_USER_PATH = "/user/create";
    private static final String LOGIN_USER_PATH = "/user/login";
    private static final String HTTP_1_1_200_OK = "HTTP/1.1 200 OK " + CRLF;
    private static final String HTTP_1_1_302_REDIRECT = "HTTP/1.1 302 REDIRECT " + CRLF;
    private static final String CONTENT_TYPE_KEY = "Content-Type: ";
    private static final String CONTENT_LENGTH_KEY = "Content-Length: ";
    private static final String LOCATION_INDEX_HTML = "Location: /index.html " + CRLF;
    private static final String LOCATION_LONGIN_FAILED_HTML = "Location: /user/login_failed.html " + CRLF;
    private static final String DEFAULT_PAGE_PATH = "/";
    private static final int OFFSET_ZERO = 0;
    private static final String DEFAULT_PAGE_MESSAGE = "Hello, World!";
    private static final String NEW_CLIENT_CONNECT_MESSAGE = "New Client Connect! Connected IP : {}, Port : {}";
    private static final String USER_ID = "userId";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie: ";
    private static final String DELIMITER = "=";
    private static final String COOKIE_PATH_POSTFIX = "; Path=/";
    private static final String USER_LIST_PATH = "/user/list";
    private static final String LOCATION_USER_LOGIN_PATH = "Location: /user/login.html";
    private static final String PREFIX_TEMPLATES = "/templates";
    private static final String SURFFIX_HTML = ".html";
    private static final String LOGINED = "logined";
    private static final String TRUE = "true";
    private static final String USER_KEY = "user";

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
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                final DataOutputStream dos = new DataOutputStream(out)
        ) {
            final HttpRequest httpRequest = HttpRequest.makeRequest(bufferedReader);
            byte[] body = DEFAULT_PAGE_MESSAGE.getBytes();
            if (httpRequest.isMethodEqual(HttpMethod.GET) && httpRequest.isPathEqual(USER_LIST_PATH)) {
                doGetUserList(body, dos, httpRequest);
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.GET) &&
                httpRequest.isPathStartingWith(LOGIN_USER_PATH) &&
                sessionManager.findSession(httpRequest.getCookie().getValueByKey(JSESSIONID)) != null
            ) {
                redirectResponse(dos, body, LOCATION_INDEX_HTML, httpRequest.getCookie());
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.GET)) {
                body = makeBody(httpRequest);
            }
            if (httpRequest.isMethodEqual(HttpMethod.POST) && httpRequest.isPathStartingWith(CREATE_USER_PATH)) {
                doPostCreateUser(httpRequest, dos, body);
                return;
            }
            if (httpRequest.isMethodEqual(HttpMethod.POST) && httpRequest.isPathStartingWith(LOGIN_USER_PATH)) {
                doPostLoginUser(httpRequest, dos, body);
                return;
            }

            standardResponse(dos, body, httpRequest);
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void doGetUserList(byte[] body, final DataOutputStream dos, final HttpRequest httpRequest) {
        final HttpCookie cookie = httpRequest.getCookie();
        if (Objects.equals(cookie.getValueByKey(LOGINED), TRUE)) {
            try {
                final TemplateLoader loader = new ClassPathTemplateLoader();
                loader.setPrefix(PREFIX_TEMPLATES);
                loader.setSuffix(SURFFIX_HTML);
                final Handlebars handlebars = new Handlebars(loader);

                final Template template = handlebars.compile(USER_LIST_PATH);

                final Map<String, Object> model = new HashMap<>();
                model.put("users", DataBase.findAll());
                final String userListPage = template.apply(model);
                body = userListPage.getBytes(StandardCharsets.UTF_8);
                response200Header(dos, body.length, httpRequest);
                responseBody(dos, body);
                return;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        redirectHeader(dos, LOCATION_USER_LOGIN_PATH + CRLF, cookie);
    }

    private void doPostCreateUser(final HttpRequest httpRequest, final DataOutputStream dos, final byte[] body) {
        final Map<String, String> queryParams = httpRequest.getBody();
        if (DataBase.isAlreadyExistId(queryParams.get(USER_ID))) {
            return;
        }
        DataBase.addUser(User.of(queryParams));
        redirectResponse(dos, body, LOCATION_INDEX_HTML, httpRequest.getCookie());
    }

    private void doPostLoginUser(final HttpRequest httpRequest, final DataOutputStream dos, final byte[] body) {
        final Map<String, String> queryParams = httpRequest.getBody();
        final HttpCookie cookie = httpRequest.getCookie();
        if (sessionManager.findSession(cookie.getValueByKey(JSESSIONID)) != null) {
            redirectResponse(dos, body, LOCATION_INDEX_HTML, cookie);
            return;
        }
        if (DataBase.isUserExist(queryParams.get(USER_ID), queryParams.get(PASSWORD))) {
            final String uuid = writeJSessionId(cookie);
            makeSession(uuid, queryParams);
            cookie.addElement(LOGINED, TRUE);
            redirectResponse(dos, body, LOCATION_INDEX_HTML, cookie);
            return;
        }
        redirectResponse(dos, body, LOCATION_LONGIN_FAILED_HTML, cookie);
    }

    private void makeSession(final String uuid, final Map<String, String> queryParams) {
        if (sessionManager.findSession(uuid) == null) {
            sessionManager.add(uuid);
            final Session session = sessionManager.findSession(uuid);
            session.setAttribute(USER_KEY, DataBase.findUserById(queryParams.get(USER_ID)).get());
        }
    }

    private String writeJSessionId(final HttpCookie cookie) {
        if (!cookie.containsKey(JSESSIONID)) {
            final UUID uuid = UUID.randomUUID();
            cookie.addElement(JSESSIONID, uuid + COOKIE_PATH_POSTFIX);
            return uuid.toString();
        }
        return cookie.getValueByKey(JSESSIONID);
    }

    private void redirectResponse(final DataOutputStream dos, final byte[] body, String location, final HttpCookie cookie) {
        redirectHeader(dos, location, cookie);
        responseBody(dos, body);
    }

    private void standardResponse(final DataOutputStream dos, final byte[] body, final HttpRequest httpRequest) {
        response200Header(dos, body.length, httpRequest);
        responseBody(dos, body);
    }

    private byte[] makeBody(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.isPathEqual(DEFAULT_PAGE_PATH)) {
            return FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
        }
        return FileIoUtils.loadFileFromClasspath(httpRequest.findFilePath());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, HttpRequest httpRequest) {
        try {
            dos.writeBytes(HTTP_1_1_200_OK);
            dos.writeBytes(CONTENT_TYPE_KEY + httpRequest.findContentType() + CRLF);
            dos.writeBytes(CONTENT_LENGTH_KEY + lengthOfBodyContent + CRLF);
            dos.writeBytes(CRLF);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void redirectHeader(final DataOutputStream dos, final String location, final HttpCookie cookie) {
        try {
            dos.writeBytes(HTTP_1_1_302_REDIRECT);
            dos.writeBytes(location);
            for (final String key : cookie.getElements().keySet()) {
                dos.writeBytes(SET_COOKIE + key + DELIMITER + cookie.getValueByKey(key) + CRLF);
            }
            dos.writeBytes(CRLF);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, OFFSET_ZERO, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
