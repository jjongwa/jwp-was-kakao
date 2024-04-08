package webserver;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import utils.FileIoUtils;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
    @Test
    void GET_index_요청에_대해_해당하는_html_파일을_전달한다() throws Exception{
        // given
        final RestTemplate restTemplate = new RestTemplate();
        final String expected = new String(FileIoUtils.loadFileFromClasspath("./templates/index.html"));

        // when
        final ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
