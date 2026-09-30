package factoriaf5.team2.goxu.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.dashboard.dtos.DashboardDTOResponse;
import factoriaf5.team2.goxu.dashboard.dtos.TopProductDTO;
import factoriaf5.team2.goxu.orders.OrderStatus;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService service;

    @InjectMocks
    private DashboardController controller;

    @Test
    void getSummary_shouldReturnSummaryFromService() {
        DashboardDTOResponse response = DashboardDTOResponse.builder()
                .totalOrders(10)
                .totalRevenue(new BigDecimal("500.00"))
                .todayOrders(2)
                .todayRevenue(new BigDecimal("80.00"))
                .ordersByStatus(Map.of(OrderStatus.DELIVERED, 8L, OrderStatus.PENDING, 2L))
                .topProducts(List.of(
                        TopProductDTO.builder()
                                .productId(1L)
                                .productName("Fabada Asturiana")
                                .unitsSold(15L)
                                .build()))
                .build();

        when(service.getSummary()).thenReturn(response);

        ResponseEntity<DashboardDTOResponse> result = controller.getSummary();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).getSummary();
    }

}