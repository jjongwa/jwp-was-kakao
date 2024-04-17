package webserver.request;

import utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    private static final String DELIMITER = "&";
    private static final String REGEX = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SPLIT_LIMIT = 2;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String EMPTY_BODY = "";
    private static final String DOSE_NOT_EXIST_KEY = "존재하지 않는 key입니다";

    private final Map<String, String> elements;

    private RequestBody(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static RequestBody readBodyIfPresent(BufferedReader bufferedReader, RequestHeader requestHeader) throws IOException {
        if (requestHeader.getElements().containsKey(CONTENT_LENGTH)) {
            final int contentLength = Integer.parseInt(requestHeader.getElements().get(CONTENT_LENGTH));
            return RequestBody.parse(IOUtils.readData(bufferedReader, contentLength));
        }
        return RequestBody.parse(EMPTY_BODY);
    }

    private static RequestBody parse(final String bodyLine) {
        if (bodyLine == null || bodyLine.isEmpty()) {
            return new RequestBody(new HashMap<>());
        }
        return makeBody(bodyLine);
    }

    private static RequestBody makeBody(final String bodyLine) {
        return Arrays.stream(bodyLine.split(DELIMITER))
                .map(field -> field.split(REGEX, SPLIT_LIMIT))
                .collect(collectingAndThen(
                        toMap(field -> field[KEY_INDEX], field -> field[VALUE_INDEX]),
                        RequestBody::new
                ));
    }

    public Map<String, String> getBody() {
        return elements;
    }

    public String getElement(final String key) {
        final String element = elements.get(key);
        if (element == null) {
            throw new IllegalArgumentException(DOSE_NOT_EXIST_KEY);
        }
        return element;
    }
}
