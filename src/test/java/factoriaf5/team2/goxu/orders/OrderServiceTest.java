package factoriaf5.team2.goxu.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.orders.dtos.OrderDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTOResponse;
import factoriaf5.team2.goxu.products.ProductEntity;
import factoriaf5.team2.goxu.products.ProductRepository;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderNotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private UserEntity user;
    private ProductEntity product;
    private OrderEntity order;
    private OrderDTOResponse orderResponse;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .name("Andrea")
                .build();

        product = ProductEntity.builder()
                .id(1L)
                .name("Fabada Asturiana")
                .price(new BigDecimal("14.50"))
                .build();

        order = OrderEntity.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("14.50"))
                .createdAt(LocalDateTime.now())
                .build();

        OrderItemDTOResponse itemResponse = OrderItemDTOResponse.builder()
                .id(1L)
                .productId(1L)
                .productName("Fabada Asturiana")
                .quantity(1)
                .unitPrice(new BigDecimal("14.50"))
                .subtotal(new BigDecimal("14.50"))
                .build();

        orderResponse = OrderDTOResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("Andrea")
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("14.50"))
                .items(List.of(itemResponse))
                .build();
    }

    @Test
    void getAll_shouldReturnAllOrdersMapped() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        List<OrderDTOResponse> result = orderService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("Andrea");
    }

    @Test
    void getByStatus_shouldReturnOrdersFilteredByStatus() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        List<OrderDTOResponse> result = orderService.getByStatus(OrderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("Andrea");
    }
    
    @Test
    void getByUser_shouldReturnOrdersFilteredByUser() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        List<OrderDTOResponse> result = orderService.getByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("Andrea");
    }

    @Test
    void getById_shouldReturnOrder_whenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderDTOResponse result = orderService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_shouldThrowNotFound_whenOrderDoesNotExist() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id 99");
    }

    @Test
    void create_shouldSaveOrderWithCalculatedTotal() {
        OrderItemDTORequest itemRequest = OrderItemDTORequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderDTORequest request = OrderDTORequest.builder()
                .userId(1L)
                .items(List.of(itemRequest))
                .build();

        OrderItemEntity itemEntity = OrderItemEntity.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("14.50"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderMapper.toEntity(itemRequest, product)).thenReturn(itemEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderDTOResponse result = orderService.create(request);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void create_shouldThrowNotFound_whenUserDoesNotExist() {
        OrderDTORequest request = OrderDTORequest.builder()
                .userId(99L)
                .items(List.of(OrderItemDTORequest.builder().productId(1L).quantity(1).build()))
                .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario no encontrado con id 99");
    }

    @Test
    void create_shouldThrowNotFound_whenProductDoesNotExist() {
        OrderDTORequest request = OrderDTORequest.builder()
                .userId(1L)
                .items(List.of(OrderItemDTORequest.builder().productId(99L).quantity(1).build()))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Producto no encontrado con id 99");
    }

    @Test
    void updateStatus_shouldSendOnTheWayNotification_whenStatusChangesToOnTheWay() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        orderService.updateStatus(1L, OrderStatus.ON_THE_WAY);

        verify(notificationService).sendOrderOnTheWay(order);
    }

    @Test
    void updateStatus_shouldSendDeliveredNotification_whenStatusChangesToDelivered() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        orderService.updateStatus(1L, OrderStatus.DELIVERED);

        verify(notificationService).sendOrderDelivered(order);
    }

    @Test
    void updateStatus_shouldChangeStatus_whenOrderExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderDTOResponse result = orderService.updateStatus(1L, OrderStatus.DELIVERED);

        assertThat(result).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void updateStatus_shouldChangeStatus_whenOrderExistsAndIsDelayed() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderDTOResponse result = orderService.updateStatus(1L, OrderStatus.DELAYED);

        assertThat(result).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELAYED);
    }

    @Test
    void updateStatus_shouldThrowNotFound_whenOrderDoesNotExist() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(99L, OrderStatus.DELIVERED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id 99");
    }

    @Test
    void markAsPaid_shouldSetPaidTrue_whenOrderExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderDTOResponse result = orderService.markAsPaid(1L);

        assertThat(result).isNotNull();
        assertThat(order.isPaid()).isTrue();
    }

    @Test
    void markAsPaid_shouldThrowNotFound_whenOrderDoesNotExist() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.markAsPaid(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id 99");
    }

    @Test
    void delete_shouldRemoveOrder_whenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.delete(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void delete_shouldThrowNotFound_whenOrderDoesNotExist() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id 99");
    }
}