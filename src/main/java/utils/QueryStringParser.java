package utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private QueryStringParser() {
    }

    private static final String QUERY_START = "\\?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final String NAME_VALUE_SEPARATOR = "=";

    public static Map<String, String> parse(final String query) {
        final String[] split = query.split(QUERY_START);
        final String[] params = split[ONE].split(PARAMETER_SEPARATOR);
        return Arrays.stream(params)
                .map(param -> param.split(NAME_VALUE_SEPARATOR))
                .collect(Collectors.toMap(pair -> pair[ZERO], pair -> pair[ONE], (a, b) -> b));
    }
}
