package pe.clubplayahonda.plh_backend.auth.security;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    @Test
    void shouldAcceptPlainTextSecret() {
        assertThatCode(() -> new JwtService("mi-secret-super-seguro-para-desarrollo-1234567890", 86400000))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptBase64Secret() {
        String base64Secret = "bXktc2VjcmV0LXN1cGVyLXNlZ3Vyby1wYXJhLWRlc2Fycm9sbG8tMTIzNDU2Nzg5MA==";

        assertThatCode(() -> new JwtService(base64Secret, 86400000))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptShortPlainTextSecret() {
        assertThatCode(() -> new JwtService("short-secret", 86400000))
                .doesNotThrowAnyException();
    }
}
