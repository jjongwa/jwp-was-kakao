package webserver.request;

public class CustomRequestLine {

    private static final String FIRST_LINE_SEPARATOR = " ";
    private static final int ZERO = 0;
    private static final int ONE = 1;

    private final CustomMethod method;
    private final CustomPath path;

    private CustomRequestLine(final CustomMethod method, final CustomPath path) {
        this.method = method;
        this.path = path;
    }

    public static CustomRequestLine from(final String firstLine) {
        final String[] split = firstLine.split(FIRST_LINE_SEPARATOR);
        return new CustomRequestLine(CustomMethod.find(split[ZERO]), CustomPath.from(split[ONE]));
    }

    public boolean checkMethod(final CustomMethod method) {
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
