package factoriaf5.team2.goxu.roles;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class RoleNameTest {

    @Test //Simplemente comprueba si los roles coinciden.
    void roleNames_matchValuesSeededInDataSql() {
        String[] expected = { "CUSTOMER", "KITCHEN", "DELIVERY", "ADMIN" };
        String[] actual = Arrays.stream(RoleName.values())
                .map(Enum::name)
                .toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }
} 