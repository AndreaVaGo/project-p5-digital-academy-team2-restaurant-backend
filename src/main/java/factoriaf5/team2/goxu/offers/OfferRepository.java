package factoriaf5.team2.goxu.offers;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Acceso a la tabla offers.
 * Los métodos heredados cubren lo necesario:
 *   - findAll(): todas las ofertas; el front separa la destacada con el campo featured
 *   - save(): crear una oferta
 */
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {

}