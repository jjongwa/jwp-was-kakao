package webserver.response;

import db.DataBase;
import model.User;
import utils.FileIoUtils;
import utils.HtmlPageBuilder;
import webserver.HttpExtensionType;

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
    private static final String DEFAULT_FILE_NAME = "templates/index.html";

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
        if (Objects.equals(path, "/")) {
            this.body = FileIoUtils.loadFileFromClasspath(DEFAULT_FILE_NAME);
            return;
        }
        if (Objects.equals(path, "/user/list")) {
            final Map<String, Object> models = new HashMap<>();
            models.put("users", DataBase.findAll());
            this.body = HtmlPageBuilder.buildUserListPage(models);
            return;
        }
        this.body = FileIoUtils.loadFileFromClasspath(HttpExtensionType.findDirectory(path) + path);
    }

    private void response200Header(final int lengthOfBodyContent) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK " + CRLF);
        dos.writeBytes("Content-Type: " + header.getHeaders().getElement("Content-Type") + CRLF);
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + CRLF);
        dos.writeBytes(CRLF);
    }

    private void responseBody(final byte[] body) throws IOException {
        dos.write(body, OFFSET_ZERO, body.length);
        dos.flush();
    }

    public void sendRedirect(final String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 REDIRECT " + CRLF);
        dos.writeBytes(location);
        processHeaders();
        dos.writeBytes(CRLF);
    }

    private void processHeaders() throws IOException {
        for (final String key : header.getCookie().getElements().keySet()) {
            dos.writeBytes("Set-Cookie: " + key + "=" + header.getCookie().getValueWithPath(key) + CRLF);
        }
    }
}
