package webserver.response;

import db.DataBase;
import utils.FileIoUtils;
import utils.HtmlPageBuilder;
import webserver.HttpExtensionType;
import webserver.HttpStatusType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private static final String DEFAULT_PAGE_MESSAGE = "Hello, World!";
    private static final int OFFSET_ZERO = 0;
    private static final String CRLF = "\r\n";
    private static final String USER_LIST_PAGE_PATH = "/user/list";
    private static final String DEFAULT_FILE_NAME = "templates/index.html";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String SET_COOKIE = "Set-Cookie: ";
    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String SPACE = " ";
    private static final String COOKIE_DELIMITER = "=";
    private static final String DEFAULT_PATH = "/";

    private final DataOutputStream dos;
    private final ResponseHeader header;
    private byte[] body;

    private HttpResponse(final ResponseHeader header, final DataOutputStream dos) {
        this.header = header;
        this.dos = dos;
        this.body = DEFAULT_PAGE_MESSAGE.getBytes();
    }

    public static HttpResponse createEmptyResponse(final OutputStream out) {
        return new HttpResponse(ResponseHeader.createEmptyHeader(), new DataOutputStream(out));
    }

    public void addHeader(final String key, final String value) {
        header.addHeader(key, value);
    }

    public void forward(final String path) throws IOException, URISyntaxException {
        forwardBody(path);
        response200Header(body.length);
        responseBody(body);
    }

    private void forwardBody(final String path) throws IOException, URISyntaxException {
        if (Objects.equals(path, DEFAULT_PATH)) {
            this.body = FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
            return;
        }
        if (Objects.equals(path, USER_LIST_PAGE_PATH)) {
            final Map<String, Object> models = new HashMap<>();
            models.put("users", DataBase.findAll());
            this.body = HtmlPageBuilder.buildUserListPage(models);
            return;
        }
        this.body = FileIoUtils.loadFileFromClasspath(HttpExtensionType.findDirectory(path) + path);
    }

    private void response200Header(final int lengthOfBodyContent) throws IOException {
        dos.writeBytes(HTTP_1_1 + SPACE + HttpStatusType.OK.getCode() + SPACE + HttpStatusType.OK.getCode() + CRLF);
        dos.writeBytes(CONTENT_TYPE + header.getHeaders().getElement(CONTENT_TYPE) + CRLF);
        dos.writeBytes(CONTENT_LENGTH + lengthOfBodyContent + CRLF);
        dos.writeBytes(CRLF);
    }

    private void responseBody(final byte[] body) throws IOException {
        dos.write(body, OFFSET_ZERO, body.length);
        dos.flush();
    }

    public void sendRedirect(final String location) throws IOException {
        dos.writeBytes(HTTP_1_1 + SPACE + HttpStatusType.REDIRECT.getCode() + SPACE + HttpStatusType.REDIRECT.getMessage() + CRLF);
        dos.writeBytes(location);
        processHeaders();
        dos.writeBytes(CRLF);
    }

    private void processHeaders() throws IOException {
        for (final String key : header.getCookie().getElements().keySet()) {
            dos.writeBytes(SET_COOKIE + key + COOKIE_DELIMITER + header.getCookie().getValueWithPath(key) + CRLF);
        }
    }
}
