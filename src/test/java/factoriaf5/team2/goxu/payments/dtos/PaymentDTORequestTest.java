package factoriaf5.team2.goxu.payments.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Tests de las validaciones de PaymentDTORequest */
class PaymentDTORequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validRequest_hasNoViolations() {
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", "12/28", "123");

        Set<ConstraintViolation<PaymentDTORequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankCardNumber_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("", "Andrea", "12/28", "123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void cardNumberWithLetters_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("4111abcd1111xxxx", "Andrea", "12/28", "123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void cardNumberTooShort_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("123456789012", "Andrea", "12/28", "123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void blankCardName_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "", "12/28", "123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void expiryDateWithWrongFormat_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", "2028-12", "123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void expiryDateWithInvalidMonth_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", "13/28", "123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void blankCvv_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", "12/28", "");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void cvvWithLetters_isRejected() {
        PaymentDTORequest dto = new PaymentDTORequest("4111111111111111", "Andrea", "12/28", "12a");

        assertFalse(validator.validate(dto).isEmpty());
    }
}