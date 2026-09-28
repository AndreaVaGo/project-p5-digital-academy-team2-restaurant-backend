package factoriaf5.team2.goxu.offers.dtos;

import java.math.BigDecimal;

import lombok.Builder;

/*
 * Datos que devuelve la API sobre una oferta (lo que SALE de la API), incluido el id
 */
@Builder
public record OfferDTOResponse(
        Long id,
        String title,
        String description,
        String image,
        String badge,
        BigDecimal price,
        boolean featured
) {
}