package webserver;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import webserver.request.CustomMethod;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
class MethodTest {

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void 입력값을_받아_해당하는_메서드를_반환할_수_있다(final String input) {
        assertDoesNotThrow(() -> CustomMethod.find(input));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"GET?", "?GET"})
    void 올바르지_않은_입력값을_받으면_예외_처리한다(final String invalidInput) {
        assertThatThrownBy(() -> CustomMethod.find(invalidInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 메서드가 존재하지 않습니다.");
    }
}
