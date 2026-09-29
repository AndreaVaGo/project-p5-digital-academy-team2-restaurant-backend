package factoriaf5.team2.goxu.orders;

import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    private OrderService service;
    private OrderController controller;

    @BeforeEach
    void setUp() {
        service = mock(OrderService.class);
        controller = new OrderController(service);
    }

    private OrderDTOResponse buildResponse(Long id, OrderStatus status) {
        return OrderDTOResponse.builder()
                .id(id)
                .userId(1L)
                .userName("Andrea")
                .status(status)
                .total(new BigDecimal("14.50"))
                .build();
    }

    @Test
    void updateStatus_returns200WithUpdatedOrder_whenStatusIsDelayed() {
        OrderDTOResponse expected = buildResponse(1L, OrderStatus.DELAYED);
        when(service.updateStatus(1L, OrderStatus.DELAYED)).thenReturn(expected);

        ResponseEntity<OrderDTOResponse> response = controller.updateStatus(1L, OrderStatus.DELAYED);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}