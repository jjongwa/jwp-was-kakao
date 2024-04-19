package webserver.controller;

import db.DataBase;
import model.User;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final String USER_ID = "userId";
    private static final String CRLF = "\r\n";
    private static final String LOCATION_INDEX_HTML = "Location: /index.html " + CRLF;


    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Map<String, String> queryParams = request.getBody();
        if (DataBase.isAlreadyExistId(queryParams.get(USER_ID))) {
            return;
        }
        DataBase.addUser(User.of(queryParams));
        response.sendRedirect(LOCATION_INDEX_HTML);
    }
}
