package factoriaf5.team2.goxu.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.profile.dtos.CustomerProfileDTOResponse;
import factoriaf5.team2.goxu.profile.dtos.CustomerProfileUpdateDTORequest;

@ExtendWith(MockitoExtension.class)
class CustomerProfileControllerTest {

    @Mock
    private CustomerProfileService customerProfileService;

    @InjectMocks
    private CustomerProfileController customerProfileController;

    @Test
    void updateProfile_shouldReturnUpdatedProfile() {
        CustomerProfileUpdateDTORequest request =
                new CustomerProfileUpdateDTORequest(
                        "López",
                        "611987654",
                        "Calle Corrida 10",
                        "33206",
                        "Gijón",
                        "https://example.com/avatar.jpg"
                );

        CustomerProfileDTOResponse response =
                CustomerProfileDTOResponse.builder()
                        .userId(1L)
                        .name("Cliente Test")
                        .email("cliente@test.com")
                        .surname("López")
                        .phone("611987654")
                        .address("Calle Corrida 10")
                        .postalCode("33206")
                        .city("Gijón")
                        .avatar("https://example.com/avatar.jpg")
                        .totalOrders(0)
                        .totalSpent(BigDecimal.ZERO)
                        .build();

        when(customerProfileService.updateProfile(1L, request))
                .thenReturn(response);

        ResponseEntity<CustomerProfileDTOResponse> result =
                customerProfileController.updateProfile(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getUserId()).isEqualTo(1L);
        assertThat(result.getBody().getName()).isEqualTo("Cliente Test");
        assertThat(result.getBody().getEmail())
                .isEqualTo("cliente@test.com");
        assertThat(result.getBody().getSurname()).isEqualTo("López");
        assertThat(result.getBody().getPhone()).isEqualTo("611987654");
        assertThat(result.getBody().getAddress())
                .isEqualTo("Calle Corrida 10");
        assertThat(result.getBody().getPostalCode()).isEqualTo("33206");
        assertThat(result.getBody().getCity()).isEqualTo("Gijón");
        assertThat(result.getBody().getAvatar())
                .isEqualTo("https://example.com/avatar.jpg");

        verify(customerProfileService)
                .updateProfile(1L, request);
    }
}
