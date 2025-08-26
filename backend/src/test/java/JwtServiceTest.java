import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.atelierlocal.security.JwtService;

@SpringBootTest(classes = com.atelierlocal.App.class)
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.withUsername("test@example.fr")
                          .password("password")
                          .roles("USER")
                          .build();
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token, "Le token ne doit pas être nul");

        String username = jwtService.extractUsername(token);
        assertEquals("test@example.fr", username, "Le username extrait doit correspondre");
    }

    @Test
    void testTokenIsValid() {
        String token = jwtService.generateToken(userDetails);
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertTrue(valid, "Le token généré doit être valide");
    }

    @Test
    void testTokenIsInvalidWithDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.withUsername("other@example.fr")
                                    .password("password")
                                    .roles("USER")
                                    .build();

        boolean valid = jwtService.isTokenValid(token, otherUser);
        assertFalse(valid, "Le token ne doit pas être valide pour un autre utilisateur");
    }
}