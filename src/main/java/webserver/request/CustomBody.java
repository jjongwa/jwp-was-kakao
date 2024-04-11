package webserver.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class CustomBody {

    private static final String DELIMITER = "&";
    private static final String REGEX = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SPLIT_LIMIT = 2;

    private final Map<String, String> elements;

    private CustomBody(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static CustomBody empty() {
        return new CustomBody(new HashMap<>());
    }

    public static CustomBody parse(final String bodyLine) {
        if (bodyLine == null) {
            return CustomBody.empty();
        }
        return Arrays.stream(bodyLine.split(DELIMITER))
                .map(field -> field.split(REGEX, SPLIT_LIMIT))
                .collect(collectingAndThen(
                        toMap(field -> field[KEY_INDEX], field -> field[VALUE_INDEX]),
                        CustomBody::new
                ));
    }

    public Map<String, String> getBody() {
        return elements;
    }
}
