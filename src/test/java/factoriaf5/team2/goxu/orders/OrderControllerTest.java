package factoriaf5.team2.goxu.orders;

import factoriaf5.team2.goxu.orders.dtos.OrderDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTORequest;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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

    @Test
    void getOrders_returns200WithAllOrders_whenNoParamsProvided() {
        List<OrderDTOResponse> orders = List.of(buildResponse(1L, OrderStatus.PENDING));
        when(service.getAll()).thenReturn(orders);

        ResponseEntity<List<OrderDTOResponse>> response = controller.getOrders(null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(orders, response.getBody());
    }

    @Test
    void getOrders_returns200WithOrdersFilteredByStatus_whenStatusProvided() {
        List<OrderDTOResponse> orders = List.of(buildResponse(1L, OrderStatus.PENDING));
        when(service.getByStatus(OrderStatus.PENDING)).thenReturn(orders);

        ResponseEntity<List<OrderDTOResponse>> response = controller.getOrders(OrderStatus.PENDING, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(orders, response.getBody());
    }

    @Test
    void getOrders_returns200WithOrdersFilteredByUser_whenUserIdProvidedAndStatusIsNull() {
        List<OrderDTOResponse> orders = List.of(buildResponse(1L, OrderStatus.PENDING));
        when(service.getByUser(1L)).thenReturn(orders);

        ResponseEntity<List<OrderDTOResponse>> response = controller.getOrders(null, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(orders, response.getBody());
    }

    @Test
    void getOrderById_returns200WithOrder_whenOrderExists() {
        OrderDTOResponse expected = buildResponse(1L, OrderStatus.PENDING);
        when(service.getById(1L)).thenReturn(expected);

        ResponseEntity<OrderDTOResponse> response = controller.getOrderById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void createOrder_returns201WithCreatedOrder_whenRequestIsValid() {
        OrderItemDTORequest item = OrderItemDTORequest.builder()
                .productId(1L)
                .quantity(2)
                .build();
        OrderDTORequest request = OrderDTORequest.builder()
                .userId(1L)
                .tableNumber("5")
                .items(List.of(item))
                .build();
        OrderDTOResponse expected = buildResponse(1L, OrderStatus.PENDING);
        when(service.create(request)).thenReturn(expected);

        ResponseEntity<OrderDTOResponse> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void markAsPaid_returns200WithPaidOrder_whenOrderExists() {
        OrderDTOResponse expected = buildResponse(1L, OrderStatus.PENDING);
        when(service.markAsPaid(1L)).thenReturn(expected);

        ResponseEntity<OrderDTOResponse> response = controller.markAsPaid(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}