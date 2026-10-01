package factoriaf5.team2.goxu.config;

import factoriaf5.team2.goxu.auth.TokenRefreshFilter;
import factoriaf5.team2.goxu.auth.TokenService;

import static org.springframework.security.config.Customizer.withDefaults;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import java.util.Arrays;
import java.util.List;
import java.time.Duration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.context.annotation.Bean;            /* Marca como productor de un bean: objeto de Java manejado por ecosistema Spring */
import org.springframework.context.annotation.Configuration; /* Marca expresamente como config */

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration 
@EnableWebSecurity 
public class SecurityConfiguration {
    
    @Value("${api-endpoint}")
    private String endpoint;

    @Value("${jwt.key}")
    private String key;

    @Bean 
    SecurityFilterChain securityFilterChain(HttpSecurity http, TokenService tokenService) 
    throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfiguration()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.POST, endpoint + "/register").permitAll()
                /*
                 * FORMULARIO DE CONTACTO: el formulario "¿Hablamos?" está en la Home, a la vista
                 * de cualquier visitante, así que enviar un mensaje (POST) no exige iniciar sesión.
                 * Consultar los mensajes (GET) sigue siendo solo para usuarios autenticados.
                 */
                .requestMatchers(HttpMethod.POST, endpoint + "/contact-messages").permitAll()
                /*
                 * LECTURA PÚBLICA: la Carta, los eventos y las ofertas son páginas públicas del front,
                 * así que cualquiera puede consultarlos (GET) sin iniciar sesión.
                 * Crear, modificar o borrar (POST, PUT, DELETE) sigue exigiendo autenticación.
                 */
                .requestMatchers(HttpMethod.GET, endpoint + "/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, endpoint + "/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, endpoint + "/offers/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(withDefaults())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.decoder(jwtDecoder())))
            .addFilterAfter(new TokenRefreshFilter(tokenService), BearerTokenAuthenticationFilter.class);

        return http.build();

    }
    //Uso key.getBytes para pasar el texto a bytes (criptografía). Lo exige Nimbus.
    //Se hila con TokenService vía JwsHeader.woth(MacAlgoritm.HS512) porque encoder necesitaba saber que algoritmo usar.
    @Bean 
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key.getBytes()));
    }
    //SecretKeySpec convierte bytes en SecretKey.
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
        decoder.setJwtValidator(new JwtTimestampValidator(Duration.ZERO));
        return decoder;
    }
    
    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of(TokenRefreshFilter.HEADER));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    // Causa error: Bean del PasswordEncoder, para inyectar en el RegisterService y hashear con BCrypt la contraseña
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}