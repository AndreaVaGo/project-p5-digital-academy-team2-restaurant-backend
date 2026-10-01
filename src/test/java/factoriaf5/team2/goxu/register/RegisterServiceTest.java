package factoriaf5.team2.goxu.register;

import factoriaf5.team2.goxu.profile.CustomerProfileRepository;
import factoriaf5.team2.goxu.register.dtos.RegisterDTORequest;
import factoriaf5.team2.goxu.register.dtos.RegisterDTOResponse;
import factoriaf5.team2.goxu.roles.RoleService;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private RoleService roleService;

        @Mock
        private CustomerProfileRepository customerProfileRepository;

        @InjectMocks
        private RegisterService registerService;

        @Test
        // Comprueba que al registrar un usuario se crea también su perfil.
        void registerUser_createsCustomerProfile() {

                RegisterDTORequest dto = new RegisterDTORequest(
                                "Juan",
                                "juan@goxu.com",
                                "password123",
                                "password123");

                UserEntity savedUser = UserEntity.builder()
                                .id(1L)
                                .name("Juan")
                                .email("juan@goxu.com")
                                .build();
                when(userRepository.findAll(
                                ArgumentMatchers.<Example<UserEntity>>any()))
                                .thenReturn(List.of());

                when(passwordEncoder.encode(dto.password()))
                                .thenReturn("encoded-password");

                when(roleService.assignDefaultRole())
                                .thenReturn(Set.of());

                when(userRepository.save(any(UserEntity.class)))
                                .thenReturn(savedUser);

                RegisterDTOResponse response = registerService.registerUser(dto);

                assertNotNull(response);

                verify(customerProfileRepository).save(
                                argThat(profile -> profile.getUser().equals(savedUser)));
        }
}