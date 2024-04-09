package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;
import webserver.request.CustomMethod;
import webserver.request.CustomRequest;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String CRLF = "\r\n";
    private static final String DEFAULT_FILE_NAME = "templates/index.html";
    private static final String CREATE_USER_PATH = "/user/create";
    private static final String HTTP_1_1_200_OK = "HTTP/1.1 200 OK " + CRLF;
    private static final String HTTP_1_1_302_REDIRECT = "HTTP/1.1 302 REDIRECT " + CRLF;
    private static final String CONTENT_TYPE_KEY = "Content-Type: ";
    private static final String CONTENT_LENGTH_KEY = "Content-Length: ";
    private static final String LOCATION_INDEX_HTML = "Location: /index.html " + CRLF;
    private static final String DEFALUT_PAGE_PATH = "/";
    private static final int OFFSET_ZERO = 0;
    private static final String DEFAULT_PAGE_MESSAGE = "Hello, World!";
    private static final String NEW_CLIENT_CONNECT_MESSAGE = "New Client Connect! Connected IP : {}, Port : {}";

    private Socket connection;

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
            final CustomRequest customRequest = CustomRequest.makeRequest(bufferedReader);
            byte[] body = DEFAULT_PAGE_MESSAGE.getBytes();

            if (customRequest.isMethodEqual(CustomMethod.GET)) {
                body = makeBody(customRequest);
            }
            if (customRequest.isMethodEqual(CustomMethod.POST) && isCreateUserRequest(customRequest)) {
                redirectResponse(dos, body);
                return;
            }
            standardResponse(dos, body, customRequest);
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
    }

    private boolean isCreateUserRequest(final CustomRequest customRequest) {
        return customRequest.isPathStartingWith(CREATE_USER_PATH) && createUser(customRequest);
    }

    private void redirectResponse(final DataOutputStream dos, final byte[] body) {
        redirectHeader(dos);
        responseBody(dos, body);
    }

    private void standardResponse(final DataOutputStream dos, final byte[] body, final CustomRequest customRequest) {
        response200Header(dos, body.length, customRequest);
        responseBody(dos, body);
    }

    private boolean createUser(final CustomRequest customRequest) {
        final Map<String, String> queryParams = customRequest.getBody();
        final User user = User.of(queryParams);
        if (DataBase.findUserById(user.getUserId()).isPresent()) {
            return false;
        }
        DataBase.addUser(user);
        return true;
    }

    private byte[] makeBody(final CustomRequest customRequest) throws Exception {
        if (customRequest.isPathEqual(DEFALUT_PAGE_PATH)) {
            return FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
        }
        return FileIoUtils.loadFileFromClasspath(customRequest.findFilePath());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, CustomRequest customRequest) {
        try {
            dos.writeBytes(HTTP_1_1_200_OK);
            dos.writeBytes(CONTENT_TYPE_KEY + customRequest.findContentType() + CRLF);
            dos.writeBytes(CONTENT_LENGTH_KEY + lengthOfBodyContent + CRLF);
            dos.writeBytes(CRLF);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void redirectHeader(DataOutputStream dos) {
        try {
            dos.writeBytes(HTTP_1_1_302_REDIRECT);
            dos.writeBytes(LOCATION_INDEX_HTML);
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
