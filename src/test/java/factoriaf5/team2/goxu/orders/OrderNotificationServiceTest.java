package factoriaf5.team2.goxu.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import factoriaf5.team2.goxu.users.UserEntity;

@ExtendWith(MockitoExtension.class)
class OrderNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private OrderNotificationService notificationService;

    private UserEntity user;
    private OrderEntity order;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .name("Andrea")
                .email("andrea@example.com")
                .build();

        order = OrderEntity.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.ON_THE_WAY)
                .total(new BigDecimal("14.50"))
                .build();
    }

    @Test
    void sendOrderOnTheWay_shouldSendEmailWithCorrectRecipientAndContent() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.sendOrderOnTheWay(order);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("andrea@example.com");
        assertThat(message.getSubject()).contains("en camino");
        assertThat(message.getText()).contains("Andrea");
    }

    @Test
    void sendOrderDelivered_shouldSendEmailWithCorrectRecipientAndContent() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.sendOrderDelivered(order);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("andrea@example.com");
        assertThat(message.getSubject()).contains("entregado");
        assertThat(message.getText()).contains("Andrea");
        assertThat(message.getText()).contains("Gracias");
    }
}