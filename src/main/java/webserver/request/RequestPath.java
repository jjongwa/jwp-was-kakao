package webserver.request;

import webserver.HttpExtensionType;

import java.util.Objects;

public class RequestPath {

    private static final int BEGIN_INDEX = 0;
    private static final String DELIMITER = "?";

    private final String resource;
    private final QueryParameter queryParameter;

    private RequestPath(final String resource, final QueryParameter queryParameter) {
        this.resource = resource;
        this.queryParameter = queryParameter;
    }

    public static RequestPath from(final String uri) {
        if (!uri.contains(DELIMITER)) {
            return new RequestPath(uri, QueryParameter.empty());
        }

        final int queryStringStartIndex = uri.indexOf(DELIMITER);
        final String resource = uri.substring(BEGIN_INDEX, queryStringStartIndex);
        final String queryString = uri.substring(queryStringStartIndex + 1);

        return new RequestPath(resource, QueryParameter.from(queryString));
    }

    public boolean isMatched(final String path) {
        return Objects.equals(this.resource, path);
    }

    public String getDirectory() {
        return HttpExtensionType.findDirectory(this.resource);
    }

    public String getExtension() {
        return HttpExtensionType.findContentType(this.resource);
    }

    public String getResource() {
        return resource;
    }

    public String getParameter(final String key) {
        return queryParameter.getParam(key);
    }
}
