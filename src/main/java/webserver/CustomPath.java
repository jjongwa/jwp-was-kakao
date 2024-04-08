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
}
