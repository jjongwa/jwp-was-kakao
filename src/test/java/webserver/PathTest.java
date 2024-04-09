package webserver;

import org.junit.jupiter.api.Test;
import webserver.request.CustomPath;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
class PathTest {

    @Test
    void 입력값을_받아_해당하는_path를_반환할_수_있다() {
        // given
        final String pathInput = "/index.html";

        // when & then
        assertDoesNotThrow(() -> new CustomPath(pathInput));
    }
}
