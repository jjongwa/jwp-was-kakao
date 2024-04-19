package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.controller.Controller;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String NEW_CLIENT_CONNECT_MESSAGE = "New Client Connect! Connected IP : {}, Port : {}";

    private final Socket connection;
    private final RequestMapping requestMapping;

    public RequestHandler(final Socket connectionSocket, final RequestMapping requestMapping) {
        this.connection = connectionSocket;
        this.requestMapping = requestMapping;
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
            final Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
    }
}
