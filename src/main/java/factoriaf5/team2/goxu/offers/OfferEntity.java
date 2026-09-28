package factoriaf5.team2.goxu.offers;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Oferta o ventaja exclusiva de la página "Privilegios Exclusivos"
 * A diferencia de los eventos, no tiene fecha: es una ventaja con una condición
 * ("los jueves", "en tu cumpleaños"...).
 * La oferta principal se marca con featured = true; el resto son la "Selección de Temporada"
 */
@Entity
@Table(name = "offers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_offer")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    /* Ruta de la imagen, como en productos y eventos */
    @Column(nullable = false)
    private String image;

    /*
     * Etiqueta de la oferta: "Exclusivo para miembros", "Cumpleaños", "Solo Miembros VIP"...
     * En el prototipo del front se llamaba label en la destacada y badge en las de temporada
     */
    @Column(nullable = false)
    private String badge;

    /* Precio por persona. Opcional: solo algunas ofertas tienen precio */
    private BigDecimal price;

    /* true para la oferta principal de la página; false para las de temporada */
    @Column(nullable = false)
    private boolean featured;
}