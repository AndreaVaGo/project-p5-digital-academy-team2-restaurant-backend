package factoriaf5.team2.goxu.users;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void save_delegatesToRepositoryAndReturnsSavedUser() {
        UserEntity toSave = UserEntity.builder().name("Juan").email("juan@goxu.com").build();
        UserEntity saved = UserEntity.builder().id(1L).name("Juan").email("juan@goxu.com").build();
        when(userRepository.save(toSave)).thenReturn(saved);

        UserEntity result = userService.save(toSave);
        assertSame(saved, result);
        verify(userRepository).save(toSave);
    }
}