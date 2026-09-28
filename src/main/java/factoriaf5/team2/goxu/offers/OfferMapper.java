package factoriaf5.team2.goxu.offers;

import factoriaf5.team2.goxu.offers.dtos.OfferDTORequest;
import factoriaf5.team2.goxu.offers.dtos.OfferDTOResponse;

/*
 * Traduce entre OfferEntity y sus DTOs, en las dos direcciones
 */
public class OfferMapper {

    /* DTO de petición -> entidad. No se asigna id: lo genera la base de datos */
    public static OfferEntity toEntity(OfferDTORequest dto) {
        return OfferEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .image(dto.image())
                .badge(dto.badge())
                .price(dto.price())
                .featured(dto.featured())
                .build();
    }

    /* Entidad guardada -> DTO de respuesta, incluido el id. */
    public static OfferDTOResponse toDTO(OfferEntity entity) {
        return OfferDTOResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .image(entity.getImage())
                .badge(entity.getBadge())
                .price(entity.getPrice())
                .featured(entity.isFeatured())
                .build();
    }
}
