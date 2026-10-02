package factoriaf5.team2.goxu.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import factoriaf5.team2.goxu.orders.OrderEntity;
import factoriaf5.team2.goxu.orders.OrderStatus;
import factoriaf5.team2.goxu.payments.dtos.PaymentDTOResponse;
import factoriaf5.team2.goxu.users.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {

    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    void toResponse_shouldMapAllFields() {
        UserEntity user = UserEntity.builder().id(1L).name("Andrea").build();

        OrderEntity order = OrderEntity.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("14.50"))
                .build();

        LocalDateTime paidAt = LocalDateTime.now();

        PaymentEntity payment = PaymentEntity.builder()
                .id(1L)
                .order(order)
                .cardHolderName("Andrea")
                .lastFourDigits("1111")
                .amount(new BigDecimal("14.50"))
                .paidAt(paidAt)
                .build();

        PaymentDTOResponse response = mapper.toResponse(payment);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getCardHolderName()).isEqualTo("Andrea");
        assertThat(response.getLastFourDigits()).isEqualTo("1111");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("14.50"));
        assertThat(response.getPaidAt()).isEqualTo(paidAt);
    }

}