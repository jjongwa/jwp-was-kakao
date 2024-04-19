package webserver.request;

import webserver.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {

    private static final String UNSUPPORTED_METHOD = "지원하지 않는 메서드입니다.";

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

    public HttpCookie getCookie() {
        return header.getCookie();
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }

    public String findContentType() {
        return this.line.getContentType();
    }

    public HttpMethod getMethod() {
        return line.getMethod();
    }

    public String getPath() {
        return line.getPath();
    }

    public String getHeader(final String key) {
        return header.getElement(key);
    }

    public String getParameter(final String key) {
        if (line.getMethod() == HttpMethod.GET) {
            return line.getParameter(key);
        }
        if (line.getMethod() == HttpMethod.POST) {
            return body.getElement(key);
        }
        throw new IllegalArgumentException(UNSUPPORTED_METHOD);
    }
}
