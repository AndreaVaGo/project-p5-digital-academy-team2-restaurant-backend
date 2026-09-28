package factoriaf5.team2.goxu.offers;

import factoriaf5.team2.goxu.offers.dtos.OfferDTORequest;
import factoriaf5.team2.goxu.offers.dtos.OfferDTOResponse;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Tests unitarios de OfferMapper.
 * No arrancan Spring ni la base de datos: solo prueban la conversión entre objetos
 */
class OfferMapperTest {

    /* Los campos del DTO de petición llegan correctamente a la entidad. */
    @Test
    void toEntity_mapsAllFields() {
        OfferDTORequest dto = new OfferDTORequest(
                "Menú Degustación 'Mar y Montaña'", "Menú de 8 pasos con maridaje de la casa",
                "/events-img/events-pairing.jpeg", "Exclusivo para miembros",
                new BigDecimal("85.00"), true);

        OfferEntity entity = OfferMapper.toEntity(dto);

        assertEquals("Menú Degustación 'Mar y Montaña'", entity.getTitle());
        assertEquals("Menú de 8 pasos con maridaje de la casa", entity.getDescription());
        assertEquals("/events-img/events-pairing.jpeg", entity.getImage());
        assertEquals("Exclusivo para miembros", entity.getBadge());
        assertEquals(new BigDecimal("85.00"), entity.getPrice());
        assertTrue(entity.isFeatured());
    }

    /* El mapper NO asigna id, y una oferta sin precio lo mantiene null */
    @Test
    void toEntity_doesNotSetIdAndKeepsNullPrice() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "Postre artesanal y brindis con cava en tu cumpleaños",
                "/events-img/events-pairing.jpeg", "Cumpleaños", null, false);

        OfferEntity entity = OfferMapper.toEntity(dto);

        assertNull(entity.getId());
        assertNull(entity.getPrice());
    }

    /* Todos los campos de la entidad guardada, incluido el id, llegan al DTO de respuesta */
    @Test
    void toDTO_mapsAllFieldsIncludingId() {
        OfferEntity entity = OfferEntity.builder()
                .id(3L)
                .title("Masterclass Gastronómica")
                .description("Acceso prioritario y descuento especial")
                .image("/events-img/events-panel-discussion.jpeg")
                .badge("Solo Miembros VIP")
                .price(null)
                .featured(false)
                .build();

        OfferDTOResponse response = OfferMapper.toDTO(entity);

        assertEquals(3L, response.id());
        assertEquals("Masterclass Gastronómica", response.title());
        assertEquals("Solo Miembros VIP", response.badge());
        assertNull(response.price());
        assertFalse(response.featured());
    }
}