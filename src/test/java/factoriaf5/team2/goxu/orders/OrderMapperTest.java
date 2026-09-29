package factoriaf5.team2.goxu.orders;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import factoriaf5.team2.goxu.orders.dtos.OrderStatusHistoryDTOResponse;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void toResponse_shouldMapOrderStatusHistoryEntityFieldsToDTOResponse() {
        LocalDateTime changedAt = LocalDateTime.of(2026, 9, 30, 12, 0);

        OrderStatusHistoryEntity entity = OrderStatusHistoryEntity.builder()
                .status(OrderStatus.ON_THE_WAY)
                .changedAt(changedAt)
                .build();

        OrderStatusHistoryDTOResponse result = mapper.toResponse(entity);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.ON_THE_WAY);
        assertThat(result.getChangedAt()).isEqualTo(changedAt);
    }

}