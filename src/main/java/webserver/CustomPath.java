package webserver;

import java.util.Objects;

public class CustomPath {

    private final String value;

    public CustomPath(final String pathInput) {
        this.value = pathInput;
    }

    public boolean isMatched(final String path) {
        return Objects.equals(this.value, path);
    }

    public String getDirectory() {
        return CustomHttpExtension.findDirectory(this.value);
    }

    public String getExtension() {
        return CustomHttpExtension.findContentType(this.value);
    }

    public String getValue() {
        return value;
    }
}
