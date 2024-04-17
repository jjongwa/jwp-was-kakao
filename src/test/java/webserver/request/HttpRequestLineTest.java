package webserver.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static webserver.request.HttpMethod.GET;

@SuppressWarnings("NonAsciiCharacters")
class HttpRequestLineTest {

    @Test
    void request의_첫번째_라인을_입력받아_올바른_requestLine_을_생성할_수_있다() {
        // given
        final String requestFirstLine = "GET /index.html HTTP/1.1";

        // when & then
        assertDoesNotThrow(() -> RequestLine.create(requestFirstLine));
    }

    @Test
    void requestLine이_가진_method가_입력받은_method와_같은지_확인할_수_있다() {
        // given
        final String requestFirstLine = "GET /index.html HTTP/1.1";
        final RequestLine requestLine = RequestLine.create(requestFirstLine);

        // when & then
        assertThat(requestLine.checkMethod(GET)).isTrue();
    }

    @Test
    void requestLine이_가진_path가_입력받은_path와_같은지_확인할_수_있다() {
        // given
        final String requestFirstLine = "GET /index.html HTTP/1.1";
        final RequestLine requestLine = RequestLine.create(requestFirstLine);
        final String expected = "/index.html";

        // when & then
        assertThat(requestLine.checkPath(expected)).isTrue();
    }

    @Test
    void requestLine이_가진_path가_입력받은_path로_시작하는지_확인할_수_있다() {
        // given
        final String requestFirstLine = "GET /index.html HTTP/1.1";
        final RequestLine requestLine = RequestLine.create(requestFirstLine);
        final String expected = "/index";

        // when & then
        assertThat(requestLine.isPathStartingWith(expected)).isTrue();
    }
}