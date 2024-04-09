package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

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
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            final InputStreamReader inputStreamReader = new InputStreamReader(in);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final CustomRequest customRequest = CustomRequest.makeRequest(bufferedReader);

            final DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello, World!".getBytes();
            if (customRequest.checkMethod(CustomMethod.GET)) {
                body = makeBody(customRequest);
            } else if (customRequest.checkMethod(CustomMethod.POST)) {
                if (customRequest.getCustomPath().getValue().startsWith("/user/create")) {
                    boolean isCreated = createUser(customRequest);
                    body = FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
                    if (isCreated) {
                        redirectHeader(dos);
                        responseBody(dos, body);
                        return;
                    }
                }
            }
            response200Header(dos, body.length, customRequest);
            responseBody(dos, body);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private boolean createUser(CustomRequest customRequest) {
        final Map<String, String> queryParams = customRequest.getCustomBody();
        final User user = User.of(queryParams);
        if (DataBase.findUserById(user.getUserId()).isPresent()) {
            return false;
        }
        DataBase.addUser(user);
        return true;
    }

    private byte[] makeBody(final CustomRequest customRequest) throws Exception {
        if (customRequest.checkPath("/")) {
            return FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
        }
        return FileIoUtils.loadFileFromClasspath(customRequest.getDirectory() + customRequest.getCustomPath().getValue());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, CustomRequest customRequest) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + customRequest.getContentType() + "\r\n");
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
