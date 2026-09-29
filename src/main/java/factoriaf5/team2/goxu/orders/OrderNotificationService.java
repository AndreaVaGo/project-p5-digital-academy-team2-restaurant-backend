package factoriaf5.team2.goxu.orders;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final JavaMailSender mailSender;

    public void sendOrderOnTheWay(OrderEntity order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Tu pedido #" + order.getId() + " está en camino");
        message.setText("Hola " + order.getUser().getName() + ",\n\n"
                + "Tu pedido está en camino. ¡Gracias por confiar en Goxu!");
        mailSender.send(message);
    }
}