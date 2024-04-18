package webserver.response;

import webserver.Headers;
import webserver.HttpCookie;

public class ResponseHeader {

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
        if (key.equals("Set-Cookie")) {
            final String[] split = value.split("=");
            cookie.addElement(split[0], split[1]);
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
