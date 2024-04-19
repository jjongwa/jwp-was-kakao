package webserver.controller;

import webserver.request.HttpMethod;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String UNSUPPORTED_METHOD_MESSAGE = "지원하지 않는 메서드입니다.";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isPost(request)) {
            doPost(request, response);
            return;
        }
        if (isGet(request)) {
            doGet(request, response);
            return;
        }
        throw new IllegalArgumentException(UNSUPPORTED_METHOD_MESSAGE);
    }

    protected boolean isPost(final HttpRequest request) {
        return request.getMethod().equals(HttpMethod.POST);
    }

    protected boolean isGet(final HttpRequest request) {
        return request.getMethod().equals(HttpMethod.GET);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        response.forward(request.getPath());
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.forward(request.getPath());
    }
}
