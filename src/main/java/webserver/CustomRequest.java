package webserver;

import java.io.BufferedReader;

public class CustomRequest {

    private final CustomMethod customMethod;
    private final CustomPath customPath;

    public CustomRequest(final BufferedReader bufferedReader) throws Exception {
        final String firstLine = bufferedReader.readLine();
        final String[] split = firstLine.split(" ");
        this.customMethod = CustomMethod.find(split[0]);
        this.customPath = new CustomPath(split[1]);
    }

    public boolean checkMethod(CustomMethod method) {
        return this.customMethod == method;
    }

    public boolean checkPath(String path) {
        return this.customPath.isMatched(path);
    }

    public CustomMethod getCustomMethod() {
        return customMethod;
    }

    public CustomPath getCustomPath() {
        return customPath;
    }
}
