package webserver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class HttpCookieTest {

    @Test
    void 쿠키_값을_파싱해_map으로_저장할_수_있다() {
        // given
        final String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; Path=/";
        final HttpCookie cookie = HttpCookie.from(cookieHeader);

        // when & then
        assertAll(
                () -> assertThat(cookie.getValue("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46"),
                () -> assertThat(cookie.getValue("Path")).isEqualTo("/")
        );
    }

    @Test
    void 쿠키에_새로운_element를_추가할_수_있다() {
        // given
        final String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; Path=/";
        final HttpCookie cookie = HttpCookie.from(cookieHeader);

        // when
        cookie.addElement("good", "dino");

        // then
        assertThat(cookie.getValue("good")).isEqualTo("dino");
    }

    @Test
    void key에_해당하는_value_가_존재하는지_여부를_확인할_수_있다() {
        // given
        final String cookieHeader = "cookie1=one; cookie2=two; cookie3=three";

        final HttpCookie cookie = HttpCookie.from(cookieHeader);

        // when

        // then
        assertAll(
                () -> assertThat(cookie.containsKey("cookie1")).isTrue(),
                () -> assertThat(cookie.containsKey("cookie2")).isTrue(),
                () -> assertThat(cookie.containsKey("cookie3")).isTrue()
        );
    }
}
