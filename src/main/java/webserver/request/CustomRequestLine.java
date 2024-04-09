package webserver.request;

public class CustomRequestLine {

    private final CustomMethod method;
    private final CustomPath path;

    private CustomRequestLine(final CustomMethod method, final CustomPath path) {
        this.method = method;
        this.path = path;
    }

    public static CustomRequestLine from(final String firstLine) {
        final String[] split = firstLine.split(" ");
        return new CustomRequestLine(CustomMethod.find(split[0]), new CustomPath(split[1]));
    }

    public boolean checkMethod(final CustomMethod method) {
        return this.method == method;
    }

    public boolean checkPath(String path) {
        return this.path.isMatched(path);
    }

    public boolean isPathStartingWith(final String path) {
        return this.path.getValue().startsWith(path);
    }

    public String getContentType() {
        return this.path.getExtension();
    }

    public String getFilePath() {
        return this.path.getDirectory() + this.path.getValue();
    }
}
