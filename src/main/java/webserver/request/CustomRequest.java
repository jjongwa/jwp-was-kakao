package webserver.request;

import utils.IOUtils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomRequest {

    private final CustomRequestLine line;
    private final CustomHeaders headers;
    private final CustomBody body;

    public CustomRequest(final CustomRequestLine line, final CustomHeaders headers, final CustomBody body) {
        this.line = line;
        this.headers = headers;
        this.body = body;
    }

    public static CustomRequest makeRequest(final BufferedReader bufferedReader) throws Exception {
        final String firstLine = bufferedReader.readLine();

        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            lines.add(line);
            line = bufferedReader.readLine();
        }

        final CustomHeaders customHeaders = new CustomHeaders(lines);

        if (!bufferedReader.ready()) {
            return new CustomRequest(
                    CustomRequestLine.from(firstLine),
                    customHeaders,
                    new CustomBody("")
            );
        }
        final int contentLength = Integer.parseInt(customHeaders.getValues().get("Content-Length"));
        return new CustomRequest(
                CustomRequestLine.from(firstLine),
                customHeaders,
                new CustomBody(IOUtils.readData(bufferedReader, contentLength))
        );
    }

    public boolean isMethodEqual(final CustomMethod customMethod) {
        return this.line.checkMethod(customMethod);
    }

    public boolean isPathStartingWith(final String path) {
        return this.line.isPathStartingWith(path);
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }

    public boolean isPathEqual(final String path) {
        return line.checkPath(path);
    }

    public String findContentType() {
        return this.line.getContentType();
    }

    public String findFilePath() {
        return line.getFilePath();
    }
}
