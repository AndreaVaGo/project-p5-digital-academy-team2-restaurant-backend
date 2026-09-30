package factoriaf5.team2.goxu.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.account.dtos.AccountDTOResponse;
import factoriaf5.team2.goxu.account.dtos.AccountUpdateDTORequest;
import factoriaf5.team2.goxu.account.dtos.ChangePasswordDTORequest;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService service;

    @InjectMocks
    private AccountController controller;

    @Test
    void getAccount_shouldReturnAccountFromService() {
        AccountDTOResponse response = AccountDTOResponse.builder()
                .id(1L)
                .name("Andrea")
                .email("andrea.test@goxu.com")
                .build();

        when(service.getAccount(1L)).thenReturn(response);

        ResponseEntity<AccountDTOResponse> result = controller.getAccount(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).getAccount(1L);
    }

    @Test
    void updateAccount_shouldReturnUpdatedAccountFromService() {
        AccountUpdateDTORequest request = new AccountUpdateDTORequest("Andrea Test", "nuevo@goxu.com");
        AccountDTOResponse response = AccountDTOResponse.builder()
                .id(1L)
                .name("Andrea Test")
                .email("nuevo@goxu.com")
                .build();

        when(service.updateAccount(1L, request)).thenReturn(response);

        ResponseEntity<AccountDTOResponse> result = controller.updateAccount(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).updateAccount(1L, request);
    }

    @Test
    void changePassword_shouldReturnNoContent() {
        ChangePasswordDTORequest request = new ChangePasswordDTORequest("actual123", "nuevaClave123");

        ResponseEntity<Void> result = controller.changePassword(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).changePassword(1L, request);
    }

}