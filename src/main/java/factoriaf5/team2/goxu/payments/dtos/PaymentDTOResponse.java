package factoriaf5.team2.goxu.payments.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDTOResponse {

    private Long id;
    private Long orderId;
    private String cardHolderName;
    private String lastFourDigits;
    private BigDecimal amount;
    private LocalDateTime paidAt;

}