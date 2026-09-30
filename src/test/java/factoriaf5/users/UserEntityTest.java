package factoriaf5.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.Set;
import org.junit.jupiter.api.Test;
import factoriaf5.team2.goxu.roles.RoleEntity;
import factoriaf5.team2.goxu.users.UserEntity;

class UserEntityTest {

    @Test // Comprueba que un usuario sea creado con todos los campos (con builder)
    void builder_createsUserWithAllFields() {
        Set<RoleEntity> roles = Set.of(new RoleEntity());

        UserEntity user = UserEntity.builder()
                .id(1L)
                .name("Juan")
                .email("juan@goxu.com")
                .password("$2a$10$hash")
                .roles(roles)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("Juan", user.getName());
        assertEquals("juan@goxu.com", user.getEmail());
        assertEquals("$2a$10$hash", user.getPassword());
        assertSame(roles, user.getRoles());
    }

    @Test // Lo crea vacio rellena con setters.
    void noArgsConstructorAndSetters_storeAllFields() {
        Set<RoleEntity> roles = Set.of(new RoleEntity());

        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setName("Ana");
        user.setEmail("ana@goxu.com");
        user.setPassword("$2a$10$otherhash");
        user.setRoles(roles);

        assertEquals(2L, user.getId());
        assertEquals("Ana", user.getName());
        assertEquals("ana@goxu.com", user.getEmail());
        assertEquals("$2a$10$otherhash", user.getPassword());
        assertSame(roles, user.getRoles());
    }
} 
