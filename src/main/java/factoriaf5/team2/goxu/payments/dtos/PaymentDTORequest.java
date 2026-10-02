package factoriaf5.team2.goxu.payments.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentDTORequest(

        @NotBlank(message = "El número de tarjeta es obligatorio") @Pattern(regexp = "^[0-9]{13,19}$", message = "El número de tarjeta debe tener entre 13 y 19 dígitos") String cardNumber,

        @NotBlank(message = "El nombre del titular es obligatorio") String cardName,

        @NotBlank(message = "La caducidad es obligatoria") @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "La caducidad debe tener el formato MM/AA") String expiryDate,

        @NotBlank(message = "El CVV es obligatorio") @Pattern(regexp = "^[0-9]{3,4}$", message = "El CVV debe tener 3 o 4 dígitos") String cvv) {
}