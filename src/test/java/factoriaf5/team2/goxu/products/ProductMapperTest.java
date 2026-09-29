package factoriaf5.team2.goxu.products;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import factoriaf5.team2.goxu.products.dtos.ProductDTORequest;
import factoriaf5.team2.goxu.products.dtos.ProductDTOResponse;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    private ProductDTORequest request;
    private ProductEntity entity;

    @BeforeEach
    void setUp() {
        request = ProductDTORequest.builder()
                .name("Fabada Asturiana")
                .description("La receta de siempre.")
                .price(new BigDecimal("16.00"))
                .image("/menu-img/fabada.png")
                .category("Especialidades")
                .available(true)
                .featured(true)
                .badgeLabel("Clásico")
                .badgeTone("highlight")
                .build();

        entity = ProductEntity.builder()
                .id(1L)
                .name("Fabada Asturiana")
                .description("La receta de siempre.")
                .price(new BigDecimal("16.00"))
                .image("/menu-img/fabada.png")
                .category("Especialidades")
                .available(true)
                .featured(true)
                .badgeLabel("Clásico")
                .badgeTone("highlight")
                .build();
    }

    @Test
    void toEntity_shouldMapAllFieldsFromRequest() {
        ProductEntity result = productMapper.toEntity(request);

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Fabada Asturiana");
        assertThat(result.getDescription()).isEqualTo("La receta de siempre.");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("16.00"));
        assertThat(result.getImage()).isEqualTo("/menu-img/fabada.png");
        assertThat(result.getCategory()).isEqualTo("Especialidades");
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.isFeatured()).isTrue();
        assertThat(result.getBadgeLabel()).isEqualTo("Clásico");
        assertThat(result.getBadgeTone()).isEqualTo("highlight");
    }

    @Test
    void toResponse_shouldMapAllFieldsFromEntity() {
        ProductDTOResponse result = productMapper.toResponse(entity);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Fabada Asturiana");
        assertThat(result.getDescription()).isEqualTo("La receta de siempre.");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("16.00"));
        assertThat(result.getImage()).isEqualTo("/menu-img/fabada.png");
        assertThat(result.getCategory()).isEqualTo("Especialidades");
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.isFeatured()).isTrue();
        assertThat(result.getBadgeLabel()).isEqualTo("Clásico");
        assertThat(result.getBadgeTone()).isEqualTo("highlight");
    }

    @Test
    void toResponse_shouldHandleNullBadgeFields() {
        ProductEntity entityWithoutBadge = ProductEntity.builder()
                .id(2L)
                .name("Refrescos")
                .description("Selección de refrescos.")
                .price(new BigDecimal("2.50"))
                .image("/menu-img/refrescos.png")
                .category("Bebidas")
                .available(true)
                .featured(false)
                .build();

        ProductDTOResponse result = productMapper.toResponse(entityWithoutBadge);

        assertThat(result.getBadgeLabel()).isNull();
        assertThat(result.getBadgeTone()).isNull();
        assertThat(result.isFeatured()).isFalse();
    }

}