package factoriaf5.team2.goxu.roles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleService roleService;

    @Test // Comprueba si getById devuelve el rol correspondiente.
    void getById_returnsRoleWhenExists() {
        RoleEntity role = buildRole(4L, RoleName.ADMIN);
        when(repository.findById(4L)).thenReturn(Optional.of(role));

        RoleEntity result = roleService.getById(4L);

        assertSame(role, result);
    }

    @Test // Salta error cuando el si el rol no existe.
    void getByName_throwsWhenRoleDoesNotExist() {

        when(repository.findByName(RoleName.KITCHEN)).thenReturn(Optional.empty());
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> roleService.getByName(RoleName.KITCHEN));
        assertTrue(exception.getMessage().contains("KITCHEN"));
    }

    @Test // Comprueba que el default role sea, efectivamente, customer.
    void assignDefaultRole_returnsSetWithOnlyCustomerRole() {
        RoleEntity customer = buildRole(1L, RoleName.CUSTOMER);
        when(repository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(customer));

        Set<RoleEntity> roles = roleService.assignDefaultRole();

        assertEquals(1, roles.size());
        assertSame(customer, roles.iterator().next());
     
        verify(repository).findByName(RoleName.CUSTOMER);
    }

    private RoleEntity buildRole(Long id, RoleName name) {
        RoleEntity role = new RoleEntity();
        role.setId(id);
        role.setName(name);
        return role;
    }
}