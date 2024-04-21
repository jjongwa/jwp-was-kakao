package webserver.controller;

import db.DataBase;
import webserver.HttpCookie;
import webserver.SessionManager;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final String USER_ID = "userId";
    private static final String PASSWORD = "password";
    private static final String CRLF = "\r\n";
    private static final String LOCATION_INDEX_HTML = "Location: /index.html " + CRLF;
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LOGINED = "logined";
    private static final String TRUE = "true";
    private static final String LOCATION_LONGIN_FAILED_HTML = "Location: /user/login_failed.html " + CRLF;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String DELIMITER = "=";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Map<String, String> queryParams = request.getBody();
        final HttpCookie cookieByRequest = request.getCookie();
        if (sessionManager.isLogined(cookieByRequest)) {
            redirectToIndexPage(response);
            return;
        }
        tryUserLogin(queryParams, cookieByRequest, response);
        response.sendRedirect(LOCATION_LONGIN_FAILED_HTML);
    }

    private void tryUserLogin(Map<String, String> queryParams, HttpCookie cookie, HttpResponse response) throws Exception {
        if (!DataBase.isUserExist(queryParams.get(USER_ID), queryParams.get(PASSWORD))) {
            response.sendRedirect(LOCATION_LONGIN_FAILED_HTML);
            return;
        }
        manageUserSession(cookie, response);
        response.addHeader(SET_COOKIE, LOGINED + DELIMITER + TRUE);
        redirectToIndexPage(response);
    }

    private void manageUserSession(HttpCookie cookie, HttpResponse response) {
        if (!cookie.containsKey(JSESSIONID)) {
            final UUID uuid = UUID.randomUUID();
            response.addHeader(SET_COOKIE, JSESSIONID + DELIMITER + uuid);
            sessionManager.makeUserSession(uuid.toString(), USER_ID);
        }
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (sessionManager.isLogined(request.getCookie())) {
            redirectToIndexPage(response);
            return;
        }
        response.addHeader(CONTENT_TYPE, request.findContentType());
        response.forward(request.getPath());
    }

    private void redirectToIndexPage(final HttpResponse response) throws IOException {
        response.sendRedirect(LOCATION_INDEX_HTML);
    }
}
