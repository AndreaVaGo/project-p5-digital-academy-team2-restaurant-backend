package factoriaf5.team2.goxu.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.profile.dtos.CustomerProfileDTOResponse;

@ExtendWith(MockitoExtension.class)
class CustomerProfileControllerTest {

    @Mock
    private CustomerProfileService service;

    @InjectMocks
    private CustomerProfileController controller;

    @Test
    void getProfile_shouldReturnProfileFromService() {
        CustomerProfileDTOResponse response = mock(CustomerProfileDTOResponse.class);
        when(service.getProfile(1L)).thenReturn(response);

        ResponseEntity<CustomerProfileDTOResponse> result = controller.getProfile(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).getProfile(1L);
    }

}