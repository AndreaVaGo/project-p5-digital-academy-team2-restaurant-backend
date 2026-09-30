package factoriaf5.team2.goxu.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;

import factoriaf5.team2.goxu.register.dtos.RegisterDTORequest;
import factoriaf5.team2.goxu.register.dtos.RegisterDTOResponse;
import factoriaf5.team2.goxu.roles.RoleEntity;
import factoriaf5.team2.goxu.roles.RoleService;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @InjectMocks
    private RegisterService registerService;

    private final RegisterDTORequest dto =
            new RegisterDTORequest("Juan", "juan@goxu.com", "12345678", "12345678");

    @Test // Comprueba si, al ingreso del email, el usuario creado se guarda con la contra y el rol customer
    void registerUser_withNewEmail_hashesPasswordAssignsRoleAndSaves() {
        Set<RoleEntity> defaultRoles = Set.of(new RoleEntity());

        // No hay ningún usuario con ese email.
        when(userRepository.findAll(ArgumentMatchers.<Example<UserEntity>>any())).thenReturn(List.of());
        when(passwordEncoder.encode("12345678")).thenReturn("$2a$10$hash");
        when(roleService.assignDefaultRole()).thenReturn(defaultRoles);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterDTOResponse response = registerService.registerUser(dto);

        assertNotNull(response);

        // Coge el usuario que se enviado revisando si guardó.
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("Juan", saved.getName());
        assertEquals("juan@goxu.com", saved.getEmail());
        assertEquals("$2a$10$hash", saved.getPassword());
        assertSame(defaultRoles, saved.getRoles());
    }

    @Test // Comprueba si se detiene el servicio ante email ya existente.
    void registerUser_withExistingEmail_returnsNullAndDoesNotSave() {
       
        when(userRepository.findAll(ArgumentMatchers.<Example<UserEntity>>any()))
                .thenReturn(List.of(new UserEntity()));

        RegisterDTORequest request = dto;
        RegisterDTOResponse response = registerService.registerUser(request);

        assertNull(response);
        // Sale antes de hacer nada, no guardando.
        verify(userRepository, never()).save(any(UserEntity.class));
        verifyNoInteractions(passwordEncoder, roleService);
    }
}