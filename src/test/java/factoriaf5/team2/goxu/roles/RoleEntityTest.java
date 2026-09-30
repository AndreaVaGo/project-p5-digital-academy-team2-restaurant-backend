package factoriaf5.team2.goxu.roles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.Set;
import org.junit.jupiter.api.Test;
import factoriaf5.team2.goxu.users.UserEntity;

class RoleEntityTest {

    @Test
    void settersAndGetters_storeAllFields() {
        Set<UserEntity> users = Set.of(new UserEntity());
        RoleEntity role = new RoleEntity();

        role.setId(1L);
        role.setName(RoleName.CUSTOMER);
        role.setUsers(users);

        assertEquals(1L, role.getId());
        assertEquals(RoleName.CUSTOMER, role.getName());
        assertSame(users, role.getUsers());
    }
}