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
    private static final String DEFAULT_FILE_NAME = "templates/index.html";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logConnectionDetails();
        try {
            processConnection();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void logConnectionDetails() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}",
                connection.getInetAddress(), connection.getPort());
    }

    private void processConnection() {
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
             final DataOutputStream dos = new DataOutputStream(out)) {

            final CustomRequest customRequest = CustomRequest.makeRequest(bufferedReader);
            byte[] body = "Hello, World!".getBytes();

            if (customRequest.isMethodEqual(CustomMethod.GET)) {
                 body  = makeBody(customRequest);
            }
            if (customRequest.isMethodEqual(CustomMethod.POST) && customRequest.isPathStartingWith("/user/create") && createUser(customRequest)) {
                redirectResponse(dos, body);
                return;
            }
            standardResponse(dos, body, customRequest);
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void redirectResponse(DataOutputStream dos, byte[] body) {
        redirectHeader(dos);
        responseBody(dos, body);
    }

    private void standardResponse(DataOutputStream dos, byte[] body, CustomRequest customRequest) {
        response200Header(dos, body.length, customRequest);
        responseBody(dos, body);
    }

    private boolean createUser(CustomRequest customRequest) {
        final Map<String, String> queryParams = customRequest.getBody();
        final User user = User.of(queryParams);
        if (DataBase.findUserById(user.getUserId()).isPresent()) {
            return false;
        }
        DataBase.addUser(user);
        return true;
    }

    private byte[] makeBody(final CustomRequest customRequest) throws Exception {
        if (customRequest.isPathEqual("/")) {
            return FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
        }
        return FileIoUtils.loadFileFromClasspath(customRequest.findFilePath());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, CustomRequest customRequest) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + customRequest.findContentType() + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void redirectHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 REDIRECT \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
