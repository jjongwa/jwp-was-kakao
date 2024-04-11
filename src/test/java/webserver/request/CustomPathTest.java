package webserver.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
class CustomPathTest {

    @Test
    void 입력값을_받아_해당하는_path를_반환할_수_있다() {
        // given
        final String pathInput = "/index.html";

        // when & then
        assertDoesNotThrow(() -> CustomPath.from(pathInput));
    }

    @Test
    void 인자로_전딜한_path와_동일한지_확인할_수_있다() {
        // given
        final String expected = "path";

        // when
        final String actual = CustomPath.from(expected).getResource();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
