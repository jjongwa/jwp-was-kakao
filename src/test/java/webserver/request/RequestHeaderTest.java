package webserver.request;

import org.junit.jupiter.api.Test;
import webserver.HttpCookie;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeaderTest {

    @Test
    void bufferdReader에서_올바르게_Request를_생성할_수_있다() {
        // Given
        final String inputData = "Host: localhost\nUser-Agent: JUnitTest\nCookie: sessionId=abc123; Path=/; dino=good\n\n";
        final BufferedReader reader = new BufferedReader(new StringReader(inputData));

        // When
        final RequestHeader requestHeader = RequestHeader.create(reader);
        final HttpCookie cookie = requestHeader.getCookie();

        // Then
        assertAll(
                () -> assertThat(requestHeader.getElement("Host")).isEqualTo("localhost"),
                () -> assertThat(requestHeader.getElement("User-Agent")).isEqualTo("JUnitTest"),
                () -> assertThat(cookie.getValue("sessionId")).isEqualTo("abc123"),
                () -> assertThat(cookie.getValue("Path")).isEqualTo("/"),
                () -> assertThat(cookie.getValue("dino")).isEqualTo("good")
        );
    }
}
