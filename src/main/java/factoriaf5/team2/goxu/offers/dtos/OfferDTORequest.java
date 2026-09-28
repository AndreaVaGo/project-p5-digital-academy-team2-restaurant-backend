package factoriaf5.team2.goxu.offers.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/*
 * Datos para crear una oferta (lo que ENTRA en la API)
 * No incluye id: lo genera la base de datos
 */
public record OfferDTORequest(

        @NotBlank(message = "El título es obligatorio")
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String description,

        @NotBlank(message = "La imagen es obligatoria")
        String image,

        /* "Exclusivo para miembros", "Cumpleaños", "Solo Miembros VIP"... */
        @NotBlank(message = "La etiqueta es obligatoria")
        String badge,

        /* Opcional. Si se indica, no puede ser negativo */
        @PositiveOrZero(message = "El precio no puede ser negativo")
        BigDecimal price,

        /* true para la oferta principal; si no se envía, vale false */
        boolean featured
) {
}