package webserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String ELEMENT_SPLIT_REGEX = "; ";
    private static final String KEY_VALUE_SPLIT_REGEX = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> elements;

    private HttpCookie() {
        this.elements = new HashMap<>();
    }

    private HttpCookie(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return new HttpCookie();
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

    public String getValueByKey(final String key) {
        if (key == null || key.isEmpty() || !elements.containsKey(key)) {
            return null;
        }
        return elements.get(key);
    }

    public Map<String, String> getElements() {
        return elements;
    }
}
