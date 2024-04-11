package webserver.request;

import utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomRequest {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String EMPTY_BODY = "";

    private final CustomRequestLine line;
    private final RequestHeaders headers;
    private final RequestBody body;

    public CustomRequest(final CustomRequestLine line, final RequestHeaders headers, final RequestBody body) {
        this.line = line;
        this.headers = headers;
        this.body = body;
    }

    public static CustomRequest makeRequest(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final RequestHeaders requestHeaders = readHeaders(bufferedReader);

        final RequestBody requestBody = readBodyIfPresent(bufferedReader, requestHeaders);

        return new CustomRequest(
                CustomRequestLine.from(firstLine),
                requestHeaders,
                requestBody
        );
    }

    private static RequestHeaders readHeaders(BufferedReader bufferedReader) {
        return new RequestHeaders(
                bufferedReader.lines()
                        .takeWhile(newLine -> newLine != null && !newLine.isEmpty())
                        .collect(Collectors.toUnmodifiableList())
        );
    }

    private static RequestBody readBodyIfPresent(BufferedReader bufferedReader, RequestHeaders requestHeaders) throws IOException {
        if (requestHeaders.getElements().containsKey(CONTENT_LENGTH)) {
            final int contentLength = Integer.parseInt(requestHeaders.getElements().get(CONTENT_LENGTH));
            return RequestBody.parse(IOUtils.readData(bufferedReader, contentLength));
        }
        return RequestBody.parse(EMPTY_BODY);
    }

    public boolean isMethodEqual(final HttpMethod httpMethod) {
        return this.line.checkMethod(httpMethod);
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
