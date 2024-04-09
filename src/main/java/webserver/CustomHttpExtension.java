package webserver;

import java.util.Arrays;

public enum CustomHttpExtension {
    HTML(".html", "text/html;charset=utf-8", "templates"),
    CSS(".css", "text/css", "static"),
    JS(".js", "text/javascript", "static"),
    ICO(".ico", "image/svg+xml", "templates"),;

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
                .orElse("text/html;charset=utf-8");
    }

    public static String findDirectory(final String path) {
        return Arrays.stream(CustomHttpExtension.values())
                .filter(customHttpExtension -> path.endsWith(customHttpExtension.extension))
                .findFirst()
                .map(customHttpExtension -> customHttpExtension.directory)
                .orElse("static");
    }
}
