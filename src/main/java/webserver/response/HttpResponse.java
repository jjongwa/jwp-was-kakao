package webserver.response;

import webserver.HttpStatusType;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class HttpResponse {

    private static final String DEFAULT_PAGE_MESSAGE = "Hello, World!";

    private HttpStatusType status;
    private final ResponseHeader header;
    private byte[] body;

    private final DataOutputStream dos;

    private HttpResponse(final ResponseHeader header, final DataOutputStream dos) {
        this.header = header;
        this.dos = dos;
        this.body = DEFAULT_PAGE_MESSAGE.getBytes();
    }

    public static HttpResponse createEmptyResponse(final OutputStream out) {
        return new HttpResponse(ResponseHeader.createEmptyHeader(), new DataOutputStream(out));
    }

    private void addHeader(final String key, final String value) {
        header.addHeader(key, value);
    }

    private void forward(final String path) {

    }

    private void forwardBody(final String body) {

    }

    private void response200Header(final int lengthOfBodyContent) {
        addHeader("Content-Type", "text/html;charset=utf-8");
        addHeader("Content-Length", String.valueOf(body.toString().length()));
    }

    private void responseBody(final byte[] body) {
        forwardBody(new String(body));

    }

    private void sendRedirect(final String location) {

    }

    private void processHeaders() {

    }
}
