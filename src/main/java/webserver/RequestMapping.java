package webserver;

import webserver.controller.Controller;
import webserver.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int ZERO_INDEX = 0;
    private static final String PATH_ALREADY_EXIST_MESSAGE = "이미 존재하는 path입니다.";
    private static final String UNDEFINED_PATH_MESSAGE = "존재하지 않는 path입니다.";

    private final Map<String, Controller> mapper = new HashMap<>();

    public void add(final String path, final Controller controller) {
        if (mapper.containsKey(path)) {
            throw new IllegalArgumentException(PATH_ALREADY_EXIST_MESSAGE);
        }
        this.mapper.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        final String resource = request.getPath().split(EXTENSION_DELIMITER)[ZERO_INDEX];
        if (mapper.containsKey(resource)) {
            return mapper.get(resource);
        }
        throw new IllegalArgumentException(UNDEFINED_PATH_MESSAGE);
    }
}
