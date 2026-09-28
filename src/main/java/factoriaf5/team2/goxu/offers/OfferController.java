package factoriaf5.team2.goxu.offers;

import factoriaf5.team2.goxu.offers.dtos.OfferDTORequest;
import factoriaf5.team2.goxu.offers.dtos.OfferDTOResponse;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/*
 * Endpoints de las ofertas
 * La ruta base toma el prefijo de la API desde application.properties (${api-endpoint})
 */
@RestController
@RequestMapping(path = "${api-endpoint}/offers")
public class OfferController {

    private final OfferService service;

    public OfferController(OfferService service) {
        this.service = service;
    }

    /*
     * GET /api/offers: todas las ofertas, para la página "Privilegios Exclusivos".
     * El front separa la oferta principal usando el campo featured.
     */
    @GetMapping("")
    public ResponseEntity<List<OfferDTOResponse>> getAllOffers() {
        return ResponseEntity.ok(service.getAllOffers());
    }

    /*
     * POST /api/offers: crea una oferta.
     * @Valid comprueba las validaciones del DTO antes de ejecutar el método (400 si fallan)
     * Responde 201 Created con la oferta creada, incluido su id.
     */
    @PostMapping("")
    public ResponseEntity<OfferDTOResponse> createOffer(@Valid @RequestBody OfferDTORequest dto) {
        OfferDTOResponse response = service.createOffer(dto);
        return ResponseEntity.status(201).body(response);
    }
}