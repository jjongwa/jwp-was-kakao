package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class SessionTest {

    private Session session;

    @BeforeEach
    public void setUp() {
        session = new Session("testSessionId");
    }

    @Test
    void 세션에_정보를_추가할_수_있다() {
        // given
        final String name = "user";
        final String value = "gooddino";

        // when
        session.setAttribute(name, value);

        // then
        assertThat(session.getAttribute(name)).isEqualTo(value);
    }

    @Test
    void 세션에_존재하는_정보를_삭제할_수_있다() {
        // given
        final String name = "user";
        final String value = "gooddino";
        session.setAttribute(name, value);

        // when
        session.removeAttribute(name);

        // then
        assertThat(session.getAttribute(name)).isNull();
    }
}
