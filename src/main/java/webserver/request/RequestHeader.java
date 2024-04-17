package webserver.request;

import webserver.HttpCookie;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {

    private static final String SPLIT_REGEX = ": ";
    private static final int VALUE_INDEX = 1;
    private static final int KEY_INDEX = 0;
    private static final String COOKIE_KEY = "Cookie";

    private final Map<String, String> elements;

    public RequestHeader(final List<String> headersInput) {
        this.elements = parseHeaders(headersInput);
    }

    public static RequestHeader create(final BufferedReader bufferedReader) {
        return new RequestHeader(
                bufferedReader.lines()
                        .takeWhile(newLine -> newLine != null && !newLine.isEmpty())
                        .collect(Collectors.toUnmodifiableList())
        );
    }

    private Map<String, String> parseHeaders(final List<String> headersInput) {
        return headersInput.stream()
                .map(input -> input.split(SPLIT_REGEX))
                .collect(Collectors.toMap(split -> split[KEY_INDEX], split -> split[VALUE_INDEX]));
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(elements.get(COOKIE_KEY));
    }

    public Map<String, String> getElements() {
        return elements;
    }

    public String getElement(final String key) {
        return elements.get(key);
    }
}
