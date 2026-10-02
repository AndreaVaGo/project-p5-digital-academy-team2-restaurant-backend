package factoriaf5.team2.goxu.payments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.orders.OrderEntity;
import factoriaf5.team2.goxu.orders.OrderStatus;
import factoriaf5.team2.goxu.payments.dtos.PaymentDTORequest;
import factoriaf5.team2.goxu.payments.dtos.PaymentDTOResponse;
import factoriaf5.team2.goxu.users.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private OrderEntity buildOrder(boolean paid) {
        UserEntity user = UserEntity.builder().id(1L).name("Andrea").build();

        return OrderEntity.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("14.50"))
                .paid(paid)
                .build();
    }

    /* Caducidad futura en formato MM/AA, calculada para que el test no caduque con el tiempo */
    private String futureExpiryDate() {
        LocalDate inTwoYears = LocalDate.now().plusYears(2);
        return inTwoYears.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    @Test
    void pay_shouldSavePaymentWithLastFourDigitsAndOrderTotal_whenCardIsValid() {
        OrderEntity order = buildOrder(false);
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", futureExpiryDate(), "123");

        PaymentEntity saved = PaymentEntity.builder().id(1L).order(order).build();
        PaymentDTOResponse expectedResponse = PaymentDTOResponse.builder().id(1L).build();

        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(saved);
        when(paymentMapper.toResponse(saved)).thenReturn(expectedResponse);

        PaymentDTOResponse result = paymentService.pay(order, dto);

        assertThat(result).isEqualTo(expectedResponse);

        ArgumentCaptor<PaymentEntity> captor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(captor.capture());

        PaymentEntity captured = captor.getValue();
        assertThat(captured.getOrder()).isEqualTo(order);
        assertThat(captured.getCardHolderName()).isEqualTo("Andrea");
        assertThat(captured.getLastFourDigits()).isEqualTo("1111");
        assertThat(captured.getAmount()).isEqualTo(order.getTotal());
        assertThat(captured.getPaidAt()).isNotNull();
    }

    @Test
    void pay_shouldThrowConflict_whenOrderIsAlreadyPaid() {
        OrderEntity order = buildOrder(true);
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", futureExpiryDate(), "123");

        assertThatThrownBy(() -> paymentService.pay(order, dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ya está pagado");
    }

    @Test
    void pay_shouldThrowBadRequest_whenCardIsExpired() {
        OrderEntity order = buildOrder(false);
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", "01/20", "123");

        assertThatThrownBy(() -> paymentService.pay(order, dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("caducado");
    }

}