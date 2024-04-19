package webserver.controller;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.addHeader(CONTENT_TYPE, request.findContentType());
        response.forward(request.getPath());
    }
}
