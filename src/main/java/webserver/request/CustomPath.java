package webserver.request;

import webserver.CustomHttpExtension;

import java.util.Objects;

public class CustomPath {

    private static final int BEGIN_INDEX = 0;
    private static final String DELIMITER = "?";

    private final String resource;
    private final QueryParameter queryParameter;

    private CustomPath(final String resource, final QueryParameter queryParameter) {
        this.resource = resource;
        this.queryParameter = queryParameter;
    }

    public static CustomPath from(final String uri) {
        if (!uri.contains(DELIMITER)) {
            return new CustomPath(uri, QueryParameter.empty());
        }

        final int queryStringStartIndex = uri.indexOf(DELIMITER);
        final String resource = uri.substring(BEGIN_INDEX, queryStringStartIndex);
        final String queryString = uri.substring(queryStringStartIndex + 1);

        return new CustomPath(resource, QueryParameter.from(queryString));
    }

    public boolean isMatched(final String path) {
        return Objects.equals(this.resource, path);
    }

    public String getDirectory() {
        return CustomHttpExtension.findDirectory(this.resource);
    }

    public String getExtension() {
        return CustomHttpExtension.findContentType(this.resource);
    }

    public String getResource() {
        return resource;
    }
}
