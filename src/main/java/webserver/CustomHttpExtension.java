package webserver;

import java.util.Arrays;

public enum CustomHttpExtension {
    HTML(".html", "text/html;charset=utf-8", "templates"),
    CSS(".css", "text/css", "static"),
    JS(".js", "text/javascript", "static"),
    ICO(".ico", "image/svg+xml", "templates"),;

    private static final String DEFALUT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String STATIC_DIRECTORY_PATH = "static";

    private final String extension;
    private final String contentType;
    private final String directory;

    CustomHttpExtension(final String extension, final String contentType, String directory) {
        this.extension = extension;
        this.contentType = contentType;
        this.directory = directory;
    }

    public static String findContentType(final String path) {
        return Arrays.stream(CustomHttpExtension.values())
                .filter(customHttpExtension -> path.endsWith(customHttpExtension.extension))
                .findFirst()
                .map(customHttpExtension -> customHttpExtension.contentType)
                .orElse(DEFALUT_CONTENT_TYPE);
    }

    public static String findDirectory(final String path) {
        return Arrays.stream(CustomHttpExtension.values())
                .filter(customHttpExtension -> path.endsWith(customHttpExtension.extension))
                .findFirst()
                .map(customHttpExtension -> customHttpExtension.directory)
                .orElse(STATIC_DIRECTORY_PATH);
    }
}
