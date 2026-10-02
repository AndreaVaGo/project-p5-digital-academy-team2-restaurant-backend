package factoriaf5.team2.goxu.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import factoriaf5.team2.goxu.roles.RoleEntity;
import factoriaf5.team2.goxu.roles.RoleName;
import factoriaf5.team2.goxu.users.UserEntity;

class SecurityUserTest {

    @Test
    void getUsername_shouldReturnUserEmail() {
        UserEntity user = UserEntity.builder()
                .email("cliente@test.com")
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        assertThat(securityUser.getUsername())
                .isEqualTo("cliente@test.com");
    }

    @Test
    void getPassword_shouldReturnUserPassword() {
        UserEntity user = UserEntity.builder()
                .password("hashedPassword")
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        assertThat(securityUser.getPassword())
                .isEqualTo("hashedPassword");
    }

    @Test
    void getAuthorities_shouldReturnEmptyList_whenUserHasNoRoles() {
        UserEntity user = UserEntity.builder()
                .roles(null)
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        assertThat(securityUser.getAuthorities())
                .isEmpty();
    }

    @Test
    void getAuthorities_shouldConvertRolesToSpringAuthorities() {
        RoleEntity customerRole = new RoleEntity();
        customerRole.setName(RoleName.CUSTOMER);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleName.ADMIN);

        UserEntity user = UserEntity.builder()
                .roles(Set.of(customerRole, adminRole))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        assertThat(securityUser.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder(
                        "ROLE_CUSTOMER",
                        "ROLE_ADMIN");
    }

    @Test
    void accountStatus_shouldAlwaysBeValid() {
        UserEntity user = new UserEntity();

        SecurityUser securityUser = new SecurityUser(user);

        assertThat(securityUser.isAccountNonExpired()).isTrue();
        assertThat(securityUser.isAccountNonLocked()).isTrue();
        assertThat(securityUser.isCredentialsNonExpired()).isTrue();
        assertThat(securityUser.isEnabled()).isTrue();
    }
}
