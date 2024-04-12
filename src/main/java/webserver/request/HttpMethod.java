package webserver.request;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST;

    private static final String INVALID_METHOD_ERROR_MESSAGE = "해당하는 메서드가 존재하지 않습니다.";

    public static HttpMethod find(final String input) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_METHOD_ERROR_MESSAGE));
    }
}
