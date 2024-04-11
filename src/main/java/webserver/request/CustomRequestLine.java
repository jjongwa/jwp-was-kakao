package webserver.request;

public class CustomRequestLine {

    private static final String FIRST_LINE_SEPARATOR = " ";
    private static final int ZERO = 0;
    private static final int ONE = 1;

    private final HttpMethod method;
    private final RequestPath path;

    private CustomRequestLine(final HttpMethod method, final RequestPath path) {
        this.method = method;
        this.path = path;
    }

    public static CustomRequestLine from(final String firstLine) {
        final String[] split = firstLine.split(FIRST_LINE_SEPARATOR);
        return new CustomRequestLine(HttpMethod.find(split[ZERO]), RequestPath.from(split[ONE]));
    }

    public boolean checkMethod(final HttpMethod method) {
        return this.method == method;
    }

    public boolean checkPath(final String path) {
        return this.path.isMatched(path);
    }

    public boolean isPathStartingWith(final String path) {
        return this.path.getResource().startsWith(path);
    }

    public String getContentType() {
        return this.path.getExtension();
    }

    public String getFilePath() {
        return this.path.getDirectory() + this.path.getResource();
    }
}
