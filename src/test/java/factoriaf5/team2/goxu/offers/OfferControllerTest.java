package factoriaf5.team2.goxu.offers;

import factoriaf5.team2.goxu.offers.dtos.OfferDTORequest;
import factoriaf5.team2.goxu.offers.dtos.OfferDTOResponse;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * Tests unitarios de OfferController
 * El Service se sustituye por un mock y se llama directamente a los métodos del Controller
 * para comprobar el código de estado y el cuerpo de la respuesta
 */
class OfferControllerTest {

    private OfferService service;
    private OfferController controller;

    /* Se ejecuta antes de cada test, para que cada uno empiece con objetos nuevos */
    @BeforeEach
    void setUp() {
        service = mock(OfferService.class);
        controller = new OfferController(service);
    }

    /* Crea un DTO de respuesta de prueba, para no repetir el builder en cada test */
    private OfferDTOResponse buildResponse(Long id, boolean featured) {
        return OfferDTOResponse.builder()
                .id(id)
                .title("Menú Degustación 'Mar y Montaña'")
                .description("Descripción de prueba")
                .image("/events-img/events-pairing.jpeg")
                .badge("Exclusivo para miembros")
                .price(new BigDecimal("85.00"))
                .featured(featured)
                .build();
    }

    /* GET de todas las ofertas: responde 200 con la lista que devuelve el Service */
    @Test
    void getAllOffers_returns200WithOffers() {
        List<OfferDTOResponse> offers = List.of(buildResponse(1L, true), buildResponse(2L, false));
        when(service.getAllOffers()).thenReturn(offers);

        ResponseEntity<List<OfferDTOResponse>> response = controller.getAllOffers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(offers, response.getBody());
    }

    /* POST de una oferta válida: responde 201 con la oferta creada */
    @Test
    void createOffer_returns201WithCreatedOffer() {
        OfferDTORequest dto = new OfferDTORequest(
                "Menú Degustación 'Mar y Montaña'", "Descripción de prueba",
                "/events-img/events-pairing.jpeg", "Exclusivo para miembros",
                new BigDecimal("85.00"), true);
        OfferDTOResponse expected = buildResponse(1L, true);
        when(service.createOffer(dto)).thenReturn(expected);

        ResponseEntity<OfferDTOResponse> response = controller.createOffer(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}