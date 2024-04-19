package webserver;

import webserver.controller.Controller;
import webserver.controller.DefaultController;
import webserver.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> mapper = new HashMap<>();

    public void add(final String path, final Controller controller) {
        this.mapper.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        final String resource = request.getPath();
        return mapper.getOrDefault(resource.split("\\.")[0], new DefaultController());
    }
}
