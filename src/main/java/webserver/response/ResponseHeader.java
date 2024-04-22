package webserver.response;

import webserver.Headers;
import webserver.HttpCookie;

public class ResponseHeader {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_DELIMITER = "=";
    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;

    private final Headers headers;
    private final HttpCookie cookie;

    private ResponseHeader(final Headers headers, final HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static ResponseHeader createEmptyHeader() {
        return new ResponseHeader(Headers.createEmptyHeaders(), HttpCookie.createEmptyCookie());
    }

    public void addHeader(final String key, final String value) {
        if (key.equals(SET_COOKIE)) {
            final String[] split = value.split(COOKIE_DELIMITER);
            cookie.addElement(split[INDEX_ZERO], split[INDEX_ONE]);
            return;
        }
        headers.addElement(key, value);
    }

    public Headers getHeaders() {
        return headers;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
