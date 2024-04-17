package webserver.request;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import utils.FileIoUtils;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    private final String testDirectory = "./src/test/resources/";

    @Test
    void GET_index_요청에_대해_해당하는_html_파일을_전달한다() throws Exception {
        // given
        final RestTemplate restTemplate = new RestTemplate();
        final String expected = new String(FileIoUtils.loadFileFromClasspath("./templates/index.html"));

        // when
        final ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void GET_요청에_대해_Request를_올바르게_만들_수_있다() throws Exception {
        final InputStream in = new FileInputStream(testDirectory + "Http_GET.txt");
        final HttpRequest request = HttpRequest.create(in);

        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo("GET"),
                () -> assertThat(request.getPath()).isEqualTo("/user/create"),
                () -> assertThat(request.getHeader("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(request.getParameter("userId")).isEqualTo("javajigi")
        );
    }

    @Test
    void POST_요청에_대해_Request를_올바르게_만들_수_있다() throws Exception {
        InputStream in = new FileInputStream(testDirectory + "Http_POST.txt");
        HttpRequest request = HttpRequest.create(in);

        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo("POST"),
                () -> assertThat(request.getPath()).isEqualTo("/user/create"),
                () -> assertThat(request.getHeader("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(request.getParameter("userId")).isEqualTo("javajigi")
        );
    }
}
