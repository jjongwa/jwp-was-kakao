package webserver.request;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeadersTest {

    @Test
    void Request_Header를_입력_받았을_때_각_key에_해당하는_value를_매칭할_수_있다() {
        // given
        final List<String> headerInput = List.of("Host: localhost:8080\n"
                , "Connection: keep-alive\n"
                , "Accept: */*\n");

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(headerInput);
        final Map<String, String> values = requestHeaders.getElements();

        // then
        assertSoftly(softly -> {
            softly.assertThat(values).containsEntry("Host", "localhost:8080\n");
            softly.assertThat(values).containsEntry("Connection", "keep-alive\n");
            softly.assertThat(values).containsEntry("Accept", "*/*\n");
        });
    }
}
