package webserver.controller;

import webserver.HttpCookie;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Objects;

public class UserListController extends AbstractController {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOGINED = "logined";
    private static final String TRUE = "true";
    private static final String LOCATION_USER_LOGIN_PATH = "Location: /user/login.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final HttpCookie cookieByRequest = request.getCookie();
        if (Objects.equals(cookieByRequest.getValue(LOGINED), TRUE)) {
            response.addHeader(CONTENT_TYPE, request.findContentType());
            response.forward(request.getPath());
            return;
        }
        response.sendRedirect(LOCATION_USER_LOGIN_PATH);
    }
}
