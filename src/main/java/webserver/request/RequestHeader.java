package webserver.request;

import webserver.Headers;
import webserver.HttpCookie;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {

    private final Headers headers;
    private final HttpCookie cookie;

    private RequestHeader(final Headers headers, final HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static RequestHeader create(final BufferedReader bufferedReader) {
        final List<String> headersInput = bufferedReader.lines()
                .takeWhile(newLine -> newLine != null && !newLine.isEmpty())
                .collect(Collectors.toUnmodifiableList());
        return new RequestHeader(Headers.fromRequestInput(headersInput), HttpCookie.fromRequestInput(headersInput));
    }

    public String getElement(final String key) {
        return headers.getElement(key);
    }

    public Map<String, String> getHeaders() {
        return headers.getElements();
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
