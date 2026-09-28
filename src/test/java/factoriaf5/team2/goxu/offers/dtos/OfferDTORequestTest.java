package factoriaf5.team2.goxu.offers.dtos;

import java.math.BigDecimal;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Tests de las validaciones de OfferDTORequest
 * Se usa directamente un Validator, sin arrancar Spring
 */
class OfferDTORequestTest {

    private static Validator validator;

    /* Se ejecuta una sola vez antes de todos los tests; por eso es static */
    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private static final String IMAGE = "/events-img/events-pairing.jpeg";

    /* Un DTO con todos los datos correctos no tiene ninguna violación */
    @Test
    void validRequest_hasNoViolations() {
        OfferDTORequest dto = new OfferDTORequest(
                "Menú Degustación", "Descripción", IMAGE,
                "Exclusivo para miembros", new BigDecimal("85.00"), true);

        assertTrue(validator.validate(dto).isEmpty());
    }

    /* Oferta sin precio: price es opcional, así que es válida */
    @Test
    void offerWithoutPrice_isValid() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "Descripción", IMAGE, "Cumpleaños", null, false);

        assertTrue(validator.validate(dto).isEmpty());
    }

    /* Título vacío: salta @NotBlank */
    @Test
    void blankTitle_isRejected() {
        OfferDTORequest dto = new OfferDTORequest(
                "", "Descripción", IMAGE, "Cumpleaños", null, false);

        assertFalse(validator.validate(dto).isEmpty());
    }

    /* Descripción vacía: salta @NotBlank */
    @Test
    void blankDescription_isRejected() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "", IMAGE, "Cumpleaños", null, false);

        assertFalse(validator.validate(dto).isEmpty());
    }

    /* Descripción de 1001 caracteres, uno más del máximo: salta @Size */
    @Test
    void tooLongDescription_isRejected() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "a".repeat(1001), IMAGE, "Cumpleaños", null, false);

        assertFalse(validator.validate(dto).isEmpty());
    }

    /* Imagen vacía: salta @NotBlank */
    @Test
    void blankImage_isRejected() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "Descripción", "", "Cumpleaños", null, false);

        assertFalse(validator.validate(dto).isEmpty());
    }

    /* Etiqueta vacía: salta @NotBlank */
    @Test
    void blankBadge_isRejected() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "Descripción", IMAGE, "", null, false);

        assertFalse(validator.validate(dto).isEmpty());
    }

    /* Precio negativo: salta @PositiveOrZero */
    @Test
    void negativePrice_isRejected() {
        OfferDTORequest dto = new OfferDTORequest(
                "Dulce Celebración", "Descripción", IMAGE, "Cumpleaños",
                new BigDecimal("-5.00"), false);

        assertFalse(validator.validate(dto).isEmpty());
    }
}