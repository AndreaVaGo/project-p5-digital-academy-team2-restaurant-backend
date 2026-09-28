package factoriaf5.team2.goxu.offers;

import factoriaf5.team2.goxu.offers.dtos.OfferDTORequest;
import factoriaf5.team2.goxu.offers.dtos.OfferDTOResponse;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * Tests unitarios de OfferService.
 * El repositorio es un mock: no se arranca Spring ni la base de datos,
 * solo se comprueba la lógica del servicio.
 */
@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    /* Repositorio falso: le decimos qué devolver en cada test */
    @Mock
    private OfferRepository offerRepository;

    /* Servicio real, al que Mockito inyecta el repositorio falso */
    @InjectMocks
    private OfferService offerService;

    /* getAllOffers devuelve todas las ofertas del repositorio convertidas a DTO */
    @Test
    void getAllOffers_returnsAllOffersAsDTOs() {
        OfferEntity featuredOffer = OfferEntity.builder()
                .id(1L)
                .title("Menú Degustación 'Mar y Montaña'")
                .description("Menú degustación de 8 pasos con maridaje de la casa.")
                .image("/events-img/events-pairing.jpeg")
                .badge("Exclusivo para miembros")
                .price(new BigDecimal("85.00"))
                .featured(true)
                .build();

        OfferEntity seasonalOffer = OfferEntity.builder()
                .id(2L)
                .title("Dulce Celebración")
                .description("Postre artesanal de cortesía en el mes de su cumpleaños.")
                .image("/events-img/events-pairing.jpeg")
                .badge("Cumpleaños")
                .price(null)
                .featured(false)
                .build();

        when(offerRepository.findAll()).thenReturn(List.of(featuredOffer, seasonalOffer));

        List<OfferDTOResponse> result = offerService.getAllOffers();

        assertEquals(2, result.size());
        assertEquals("Menú Degustación 'Mar y Montaña'", result.get(0).title());
        assertTrue(result.get(0).featured());
        assertEquals("Dulce Celebración", result.get(1).title());
        verify(offerRepository, times(1)).findAll();
    }

    /* createOffer guarda la oferta y devuelve el DTO con el id generado */
    @Test
    void createOffer_savesAndReturnsOfferWithId() {
        OfferDTORequest request = new OfferDTORequest(
                "Masterclass Gastronómica",
                "Acceso prioritario a la próxima sesión con nuestro Chef Ejecutivo.",
                "/events-img/events-panel-discussion.jpeg",
                "Solo Miembros VIP",
                new BigDecimal("30.00"),
                false);

        /* Lo que "devuelve la base de datos" al guardar: la misma oferta, ya con id */
        OfferEntity savedOffer = OfferEntity.builder()
                .id(3L)
                .title(request.title())
                .description(request.description())
                .image(request.image())
                .badge(request.badge())
                .price(request.price())
                .featured(request.featured())
                .build();

        when(offerRepository.save(any(OfferEntity.class))).thenReturn(savedOffer);

        OfferDTOResponse result = offerService.createOffer(request);

        assertEquals(3L, result.id());
        assertEquals("Masterclass Gastronómica", result.title());
        assertEquals(new BigDecimal("30.00"), result.price());
        verify(offerRepository, times(1)).save(any(OfferEntity.class));
    }
}