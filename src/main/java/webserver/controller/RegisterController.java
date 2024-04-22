package webserver.controller;

import db.DataBase;
import model.User;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String USER_ID = "userId";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String CRLF = "\r\n";
    private static final String ALREADY_EXIST_ID_MESSAGE = "이미 존재하는 아이디입니다.";
    private static final String LOCATION_INDEX_HTML = "Location: /index.html " + CRLF;


    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        if (DataBase.isAlreadyExistId(request.getBody(USER_ID))) {
            throw new IllegalArgumentException(ALREADY_EXIST_ID_MESSAGE);
        }
        DataBase.addUser(new User(
                request.getBody(USER_ID),
                request.getBody(PASSWORD),
                request.getBody(NAME),
                request.getBody(EMAIL)
        ));
        response.sendRedirect(LOCATION_INDEX_HTML);
    }
}
