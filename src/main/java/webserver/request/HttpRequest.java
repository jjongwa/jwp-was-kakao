package webserver.request;

import webserver.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private final RequestLine line;
    private final RequestHeader header;
    private final RequestBody body;

    private HttpRequest(final RequestLine line, final RequestHeader header, final RequestBody body) {
        this.line = line;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest create(final InputStream in) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        final RequestLine requestLine = RequestLine.create(bufferedReader.readLine());
        final RequestHeader requestHeader = RequestHeader.create(bufferedReader);
        final RequestBody requestBody = RequestBody.readBodyIfPresent(bufferedReader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    public HttpMethod getMethod() {
        return line.getMethod();
    }

    public String getPath() {
        return line.getPath();
    }

    public String findContentType() {
        return this.line.getContentType();
    }

    public String getParameter(final String key) {
        return line.getParameter(key);
    }

    public String getHeader(final String key) {
        return header.getElement(key);
    }

    public HttpCookie getCookie() {
        return header.getCookie();
    }

    public String getBody(final String key) {
        return body.getElement(key);
    }
}
