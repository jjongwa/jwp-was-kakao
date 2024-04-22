package webserver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class HeadersTest {

    @Test
    void 헤더_생성_시_쿠키는_제외한다() {
        // given
        final List<String> headersInput = List.of(
                "Host: localhost:8080\n"
                , "Connection: keep-alive\n"
                , "Accept: */*\n"
                , "Cookie: JSESSIONID=1234\n"
        );

        // when
        final Headers headers = Headers.fromRequestInput(headersInput);

        // then
        assertAll(
                () -> assertThat(headers.getElement("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getElement("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(headers.getElement("Accept")).isEqualTo("*/*"),
                () -> assertThatThrownBy(() -> headers.getElement("Cookie"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("해더에 해당 키에 대한 값이 존재하지 않습니다.")
        );
    }
}
