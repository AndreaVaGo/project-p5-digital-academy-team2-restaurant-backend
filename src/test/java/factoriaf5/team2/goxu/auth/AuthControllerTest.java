package factoriaf5.team2.goxu.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import factoriaf5.team2.goxu.auth.dtos.TokenAuthDTOResponse;

import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepository;

    @Test // Comprueba si responde correctamente 200 y si se emite el token.
    void token_returnsOkWithGeneratedToken() {
        when(tokenService.generateToken(authentication)).thenReturn("fake.jwt.token");

        ResponseEntity<TokenAuthDTOResponse> response = authController.token(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("fake.jwt.token", response.getBody().token());
    }

    @Test // Comprueba que el controlador delega en TokenService.
    void token_delegatesToTokenServiceOnceWithSameAuthentication() {
        when(tokenService.generateToken(authentication)).thenReturn("fake.jwt.token");

        authController.token(authentication);

        // El controlador NO genera el token: se lo pide a TokenService.
        verify(tokenService, times(1)).generateToken(authentication);
        verifyNoMoreInteractions(tokenService);
    }

@Test
void me_returnsAuthenticatedUserData() {

    UserEntity user = UserEntity.builder()
            .id(1L)
            .name("Juan")
            .email("juan@authentication.com")
            .build();

    when(authentication.getName()).thenReturn("juan@authentication.com");
    when(userRepository.findByEmail("juan@authentication.com"))
            .thenReturn(Optional.of(user));

    ResponseEntity<Map<String, Object>> response = authController.me(authentication);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    assertEquals(1L, response.getBody().get("id"));
    assertEquals("Juan", response.getBody().get("name"));
    assertEquals("juan@authentication.com", response.getBody().get("email"));
}
    @Test // Comprueba que no devuelva tokens, porque no le corresponde (/me)
    void me_doesNotIssueTokens() {
        when(authentication.getName()).thenReturn("juan@authentication.com");

        authController.me(authentication);

        verifyNoInteractions(tokenService);
    }
}