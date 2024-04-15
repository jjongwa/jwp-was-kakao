package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {
    private SessionManager sessionManager;

    @BeforeEach
    public void setUp() {
        sessionManager = SessionManager.getInstance();
    }

    @Test
    void 새로운_세션을_추가할_수_있다() {
        // given
        final String sessionId = "uniqueSessionId";

        // when
        final Session session = sessionManager.add(sessionId);

        assertThat(sessionManager.findSession(sessionId)).isEqualTo(session);
    }

    @Test
    void 세션을_삭제할_수_있다() {
        // given
        final String sessionId = "uniqueSessionId";
        sessionManager.add(sessionId);

        // when
        sessionManager.remove(sessionId);
        assertThat(sessionManager.findSession(sessionId)).isNull();
    }
}
