package webserver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class HttpCookieTest {

    private static final List<String> HEADERS_INPUT = List.of(
            "Host: localhost:8080\n"
            , "Connection: keep-alive\n"
            , "Accept: */*\n"
            , "Cookie: JSESSIONID=1234; Good=Dino; Path=/\n"
    );

    @Test
    void 헤더_리스트에서_쿠키를_만들_수_있다() {
        // when
        final HttpCookie httpCookie = HttpCookie.fromRequestInput(HEADERS_INPUT);

        // then
        assertAll(
                () -> assertThat(httpCookie.getValue("JSESSIONID")).isEqualTo("1234"),
                () -> assertThat(httpCookie.getValue("Good")).isEqualTo("Dino")
        );
    }

    @Test
    void 쿠키에_새로운_element를_추가할_수_있다() {
        // given
        final HttpCookie httpCookie = HttpCookie.fromRequestInput(HEADERS_INPUT);

        // when
        httpCookie.addElement("good", "dino");

        // then
        assertThat(httpCookie.getValue("good")).isEqualTo("dino");
    }

    @Test
    void key에_해당하는_value_가_존재하는지_여부를_확인할_수_있다() {
        // when
        final HttpCookie httpCookie = HttpCookie.fromRequestInput(HEADERS_INPUT);

        // then
        assertAll(
                () -> assertThat(httpCookie.containsKey("JSESSIONID")).isTrue(),
                () -> assertThat(httpCookie.containsKey("Good")).isTrue(),
                () -> assertThat(httpCookie.containsKey("Bad")).isFalse()
        );
    }
}
