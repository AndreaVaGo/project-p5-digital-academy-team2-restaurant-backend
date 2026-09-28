package factoriaf5.team2.goxu.offers;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import factoriaf5.team2.goxu.offers.dtos.OfferDTORequest;
import factoriaf5.team2.goxu.offers.dtos.OfferDTOResponse;

/*
 * Servicio de ofertas: contiene la lógica de negocio entre el controller y el repositorio
 * Siguiendo la convención del equipo, no tiene interfaz y recibe sus dependencias por constructor
 */
@Service
public class OfferService {

    private final OfferRepository offerRepository;

    /* Inyección por constructor: Spring pasa el repositorio al crear el servicio */
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    /*
     * Devuelve todas las ofertas convertidas a DTO de respuesta
     * El front separa la oferta principal usando el campo featured
     */
    public List<OfferDTOResponse> getAllOffers() {
        return offerRepository.findAll()
                .stream()
                .map(OfferMapper::toDTO)
                .toList();
    }

    /*
     * Crea una oferta nueva: convierte el DTO en entidad, la guarda
     * y devuelve la entidad guardada (ya con id) como DTO de respuesta
     * @Transactional porque es una operación de escritura
     */
    @Transactional
    public OfferDTOResponse createOffer(OfferDTORequest request) {
        OfferEntity offer = OfferMapper.toEntity(request);
        OfferEntity savedOffer = offerRepository.save(offer);
        return OfferMapper.toDTO(savedOffer);
    }
}