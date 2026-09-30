package factoriaf5.team2.goxu.auth;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import factoriaf5.team2.goxu.auth.dtos.TokenAuthDTOResponse;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
public class AuthController {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthController(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    // Flujo I: cuando llega aquí, Spring YA ha comprobado el email y la contraseña
    // con la base de datos.
    @PostMapping("/token")
    public ResponseEntity<TokenAuthDTOResponse> token(Authentication authentication) {
        String token = tokenService.generateToken(authentication);
        return ResponseEntity.ok(new TokenAuthDTOResponse(token));
    }

    // Flujo II: solo responde con un token válido y devuelve quién eres.
@GetMapping("/me")
public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
    Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());

    if (user.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    UserEntity currentUser = user.get();

    return ResponseEntity.ok(Map.of(
            "id", currentUser.getId(),
            "name", currentUser.getName(),
            "email", currentUser.getEmail()
    ));
}
}

