package webserver;

import utils.IOUtils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomRequest {

    private final CustomMethod customMethod;
    private final CustomPath customPath;
    private final CustomHeaders customHeaders;
    private final CustomBody customBody;

    public CustomRequest(
            final CustomMethod customMethod,
            final CustomPath customPath,
            final CustomHeaders customHeaders,
            final CustomBody customBody
    ) {
        this.customMethod = customMethod;
        this.customPath = customPath;
        this.customHeaders = customHeaders;
        this.customBody = customBody;
    }

    public static CustomRequest makeRequest(final BufferedReader bufferedReader) throws Exception {
        final String firstLine = bufferedReader.readLine();
        final String[] split = firstLine.split(" ");

        final List<String> lines = new ArrayList<>();

        String line = bufferedReader.readLine();

        while (!"".equals(line)) {
            lines.add(line);
            line = bufferedReader.readLine();
        }

        final CustomHeaders customHeaders = new CustomHeaders(lines);

        if (!bufferedReader.ready()) {
            return new CustomRequest(
                    CustomMethod.find(split[0]),
                    new CustomPath(split[1]),
                    customHeaders,
                    new CustomBody("")
            );
        }
        final int contentLength = Integer.parseInt(customHeaders.getValues().get("Content-Length"));
        return new CustomRequest(
                CustomMethod.find(split[0]),
                new CustomPath(split[1]),
                customHeaders,
                new CustomBody(IOUtils.readData(bufferedReader, contentLength))
        );
    }

    public boolean checkMethod(CustomMethod method) {
        return this.customMethod == method;
    }

    public boolean checkPath(String path) {
        return this.customPath.isMatched(path);
    }

    public String getDirectory() {
        return this.customPath.getDirectory();
    }

    public String getContentType() {
        return this.customPath.getExtension();
    }

    public Map<String, String> getQueryParams() {
        return this.customPath.getQueryParams();
    }

    public CustomMethod getCustomMethod() {
        return customMethod;
    }

    public CustomPath getCustomPath() {
        return customPath;
    }
}
