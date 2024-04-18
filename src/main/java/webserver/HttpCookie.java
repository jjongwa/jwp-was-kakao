package webserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String SPLIT_REGEX = ": ";
    private static final String ELEMENT_SPLIT_REGEX = "; ";
    private static final String KEY_VALUE_SPLIT_REGEX = "=";
    private static final String COOKIE_PATH_SUFFIX = "; Path=/";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final Supplier<String> EMPTY_STRING = ""::toString;
    private static final String REQUEST_COOKIE_KEY = "Cookie";

    private final Map<String, String> elements;

    private HttpCookie(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static HttpCookie createEmptyCookie() {
        return new HttpCookie(new HashMap<>());
    }

    public static HttpCookie fromRequestInput(final List<String> headersInput) {
        return HttpCookie.parse(headersInput.stream()
                .map(input -> input.split(SPLIT_REGEX))
                .filter(split -> split[KEY_INDEX].equals(REQUEST_COOKIE_KEY))
                .findFirst()
                .map(split -> split[VALUE_INDEX])
                .orElseGet(EMPTY_STRING)
                .trim()
        );
    }

    private static HttpCookie parse(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return HttpCookie.createEmptyCookie();
        }
        return new HttpCookie(Arrays.stream(cookieHeader.split(ELEMENT_SPLIT_REGEX))
                .map(element -> element.split(KEY_VALUE_SPLIT_REGEX))
                .collect(Collectors.toMap(element -> element[KEY_INDEX], element -> element[VALUE_INDEX]))
        );
    }

    public void addElement(final String key, final String value) {
        elements.put(key, value);
    }

    public boolean containsKey(final String key) {
        return elements.containsKey(key);
    }

    public String getValue(final String key) {
        if (key == null || key.isEmpty() || !elements.containsKey(key)) {
            return null;
        }
        return elements.get(key);
    }

    public String getValueWithPath(final String key) {
        if (key == null || key.isEmpty() || !elements.containsKey(key)) {
            return null;
        }
        return elements.get(key) + COOKIE_PATH_SUFFIX;
    }

    public Map<String, String> getElements() {
        return elements;
    }
}
