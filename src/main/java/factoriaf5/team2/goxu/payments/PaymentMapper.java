package factoriaf5.team2.goxu.payments;

import org.springframework.stereotype.Component;

import factoriaf5.team2.goxu.payments.dtos.PaymentDTOResponse;

@Component
public class PaymentMapper {

    public PaymentDTOResponse toResponse(PaymentEntity entity) {
        return PaymentDTOResponse.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .cardHolderName(entity.getCardHolderName())
                .lastFourDigits(entity.getLastFourDigits())
                .amount(entity.getAmount())
                .paidAt(entity.getPaidAt())
                .build();
    }

}