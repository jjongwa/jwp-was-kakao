package webserver;

import utils.QueryStringParser;

import java.util.Map;

public class CustomBody {

    private final Map<String, String> elements;

    public CustomBody(final String body) {
        this.elements = QueryStringParser.parse(body);
    }

    public Map<String, String> getBody() {
        return elements;
    }
}
