package webserver;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Headers {

    private static final String SPLIT_REGEX = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String REQUEST_COOKIE_KEY = "Cookie";
    private static final String HEADER_NO_SUCH_KEY_MESSAGE = "해더에 해당 키에 대한 값이 존재하지 않습니다.";

    private final Map<String, String> elements;

    private Headers(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static Headers fromRequestInput(final List<String> headersInput) {
        return new Headers(headersInput.stream()
                .map(input -> input.split(SPLIT_REGEX))
                .filter(split -> !Objects.equals(split[KEY_INDEX], REQUEST_COOKIE_KEY))
                .collect(Collectors.toMap(split -> split[KEY_INDEX], split -> split[VALUE_INDEX].trim()))
        );
    }

    public Map<String, String> getElements() {
        return elements;
    }

    public String getElement(final String key) {
        if (key == null || key.isEmpty() || !elements.containsKey(key)) {
            throw new IllegalArgumentException(HEADER_NO_SUCH_KEY_MESSAGE);
        }
        return elements.get(key);
    }
}
