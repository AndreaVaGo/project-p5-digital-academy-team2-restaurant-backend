package factoriaf5.team2.goxu.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.orders.dtos.OrderDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTORequest;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService service;

    @InjectMocks
    private OrderController controller;

    private OrderDTOResponse response;

    @BeforeEach
    void setUp() {
        response = OrderDTOResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("Andrea")
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("51.50"))
                .build();
    }

    @Test
    void getOrders_shouldReturnByStatus_whenStatusGiven() {
        when(service.getByStatus(OrderStatus.ON_THE_WAY)).thenReturn(List.of(response));

        ResponseEntity<List<OrderDTOResponse>> result = controller.getOrders(OrderStatus.ON_THE_WAY, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        verify(service).getByStatus(OrderStatus.ON_THE_WAY);
    }

    @Test
    void getOrders_shouldReturnByUser_whenUserIdGiven() {
        when(service.getByUser(1L)).thenReturn(List.of(response));

        ResponseEntity<List<OrderDTOResponse>> result = controller.getOrders(null, 1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        verify(service).getByUser(1L);
    }

    @Test
    void getOrders_shouldReturnAll_whenNoFiltersGiven() {
        when(service.getAll()).thenReturn(List.of(response));

        ResponseEntity<List<OrderDTOResponse>> result = controller.getOrders(null, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        verify(service).getAll();
    }

    @Test
    void getOrderById_shouldReturnOrderFromService() {
        when(service.getById(1L)).thenReturn(response);

        ResponseEntity<OrderDTOResponse> result = controller.getOrderById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void createOrder_shouldReturnCreatedStatus() {
        OrderItemDTORequest item = OrderItemDTORequest.builder().productId(1L).quantity(2).build();
        OrderDTORequest request = OrderDTORequest.builder().userId(1L).items(List.of(item)).build();

        when(service.create(request)).thenReturn(response);

        ResponseEntity<OrderDTOResponse> result = controller.createOrder(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateStatus_shouldReturnUpdatedOrder() {
        when(service.updateStatus(1L, OrderStatus.ON_THE_WAY)).thenReturn(response);

        ResponseEntity<OrderDTOResponse> result = controller.updateStatus(1L, OrderStatus.ON_THE_WAY);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void markAsPaid_shouldReturnUpdatedOrder() {
        when(service.markAsPaid(1L)).thenReturn(response);

        ResponseEntity<OrderDTOResponse> result = controller.markAsPaid(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteOrder_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteOrder(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

}