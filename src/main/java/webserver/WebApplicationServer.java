package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.controller.DefaultController;
import webserver.controller.LoginController;
import webserver.controller.RegisterController;
import webserver.controller.UserListController;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebApplicationServer {
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int CORE_POOL_SIZE = 100;
    private static final int MAXIMUM_POOL_SIZE = 250;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final int WORK_QUEUE_CAPACITY = 100;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        final RequestMapping requestMapping = initRequestMapping();
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(WORK_QUEUE_CAPACITY)
        );

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                threadPoolExecutor.execute(new RequestHandler(connection, requestMapping));
            }
        }
    }

    private static RequestMapping initRequestMapping() {
        final RequestMapping requestMapping = new RequestMapping();
        requestMapping.add("/", new DefaultController());
        requestMapping.add("/user/create", new RegisterController());
        requestMapping.add("/user/login", new LoginController());
        requestMapping.add("/user/list", new UserListController());
        return requestMapping;
    }
}
