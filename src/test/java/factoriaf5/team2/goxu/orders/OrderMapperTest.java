package factoriaf5.team2.goxu.orders;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTOResponse;
import factoriaf5.team2.goxu.products.ProductEntity;
import factoriaf5.team2.goxu.users.UserEntity;

class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapper();

    private ProductEntity product;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        product = ProductEntity.builder()
                .id(1L)
                .name("Fabada Asturiana")
                .price(new BigDecimal("16.00"))
                .build();

        user = UserEntity.builder()
                .id(1L)
                .name("Andrea")
                .build();
    }

    @Test
    void toEntity_shouldMapDtoAndProductToOrderItemEntity() {
        OrderItemDTORequest dto = OrderItemDTORequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderItemEntity result = orderMapper.toEntity(dto, product);

        assertThat(result.getProduct()).isEqualTo(product);
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnitPrice()).isEqualTo(new BigDecimal("16.00"));
    }

    @Test
    void toResponse_shouldMapOrderItemEntityAndCalculateSubtotal() {
        OrderItemEntity item = OrderItemEntity.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("16.00"))
                .build();

        OrderItemDTOResponse result = orderMapper.toResponse(item);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Fabada Asturiana");
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnitPrice()).isEqualTo(new BigDecimal("16.00"));
        assertThat(result.getSubtotal()).isEqualTo(new BigDecimal("32.00"));
    }

    @Test
    void toResponse_shouldMapOrderEntityWithItems() {
        OrderItemEntity item = OrderItemEntity.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("16.00"))
                .build();

        OrderEntity order = OrderEntity.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .paid(false)
                .total(new BigDecimal("32.00"))
                .createdAt(LocalDateTime.now())
                .items(List.of(item))
                .build();

        OrderDTOResponse result = orderMapper.toResponse(order);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUserName()).isEqualTo("Andrea");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.isPaid()).isFalse();
        assertThat(result.getTotal()).isEqualTo(new BigDecimal("32.00"));
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("Fabada Asturiana");
    }

}