package webserver.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String PARAMETER_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> params;

    private QueryParameter(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParameter empty() {
        return new QueryParameter(new HashMap<>());
    }

    public static QueryParameter from(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        if (queryString != null) {
            final String[] queries = queryString.split(QUERY_STRING_SEPARATOR);
            initParams(queries, params);
        }
        return new QueryParameter(params);
    }

    private static void initParams(final String[] queries, final Map<String, String> params) {
        Arrays.stream(queries)
                .map(param -> param.split(PARAMETER_DELIMITER))
                .forEach(splitParam -> {
                    final String key = splitParam[KEY_INDEX];
                    final String value = splitParam[VALUE_INDEX];
                    params.put(key, value);
                });
    }

    public Map<String, String> getParams() {
        return params;
    }
}
