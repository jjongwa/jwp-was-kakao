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
    private final RequestHeader headers;
    private final RequestBody body;

    private HttpRequest(final RequestLine line, final RequestHeader headers, final RequestBody body) {
        this.line = line;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest create(final InputStream in) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        final RequestLine requestLine = RequestLine.create(bufferedReader.readLine());
        final RequestHeader requestHeader = RequestHeader.create(bufferedReader);
        final RequestBody requestBody = RequestBody.readBodyIfPresent(bufferedReader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    public boolean isMethodEqual(final HttpMethod httpMethod) {
        return this.line.checkMethod(httpMethod);
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    public boolean isPathStartingWith(final String path) {
        return this.line.isPathStartingWith(path);
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }

    public boolean isPathEqual(final String path) {
        return line.checkPath(path);
    }

    public String findContentType() {
        return this.line.getContentType();
    }

    public String findFilePath() {
        return line.getFilePath();
    }

    public String getMethod() {
        return line.getMethod().name();
    }

    public String getPath() {
        return line.getPath();
    }

    public String getHeader(final String key) {
        return headers.getElement(key);
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
