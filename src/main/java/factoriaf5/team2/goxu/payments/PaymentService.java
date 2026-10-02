package factoriaf5.team2.goxu.payments;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.orders.OrderEntity;
import factoriaf5.team2.goxu.payments.dtos.PaymentDTORequest;
import factoriaf5.team2.goxu.payments.dtos.PaymentDTOResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentDTOResponse pay(OrderEntity order, PaymentDTORequest dto) {

        if (order.isPaid()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El pedido ya está pagado");
        }

        validateExpiryDate(dto.expiryDate());

        String lastFourDigits = dto.cardNumber().substring(dto.cardNumber().length() - 4);

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .cardHolderName(dto.cardName())
                .lastFourDigits(lastFourDigits)
                .amount(order.getTotal())
                .paidAt(LocalDateTime.now())
                .build();

        PaymentEntity saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    private void validateExpiryDate(String expiryDate) {
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);

        YearMonth expiry = YearMonth.of(year, month);
        if (expiry.isBefore(YearMonth.from(LocalDate.now()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tarjeta ha caducado");
        }
    }

}