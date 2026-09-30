package factoriaf5.team2.goxu.products;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.products.dtos.ProductDTORequest;
import factoriaf5.team2.goxu.products.dtos.ProductDTOResponse;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductEntity product;
    private ProductDTOResponse productResponse;

    @BeforeEach
    void setUp() {
        product = ProductEntity.builder()
                .id(1L)
                .name("Fabada Asturiana")
                .description("La receta de siempre.")
                .price(new BigDecimal("16.00"))
                .image("/menu-img/fabada.png")
                .category("Especialidades")
                .available(true)
                .featured(true)
                .build();

        productResponse = ProductDTOResponse.builder()
                .id(1L)
                .name("Fabada Asturiana")
                .category("Especialidades")
                .price(new BigDecimal("16.00"))
                .build();
    }

    @Test
    void getAll_shouldReturnAllProductsMapped() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductDTOResponse> result = productService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Fabada Asturiana");
    }

    @Test
    void getFeatured_shouldReturnOnlyFeaturedProducts() {
        when(productRepository.findByFeaturedTrue()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductDTOResponse> result = productService.getFeatured();

        assertThat(result).hasSize(1);
        verify(productRepository).findByFeaturedTrue();
    }

    @Test
    void getByCategory_shouldReturnProductsInThatCategory() {
        when(productRepository.findByCategory("Especialidades")).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductDTOResponse> result = productService.getByCategory("Especialidades");

        assertThat(result).hasSize(1);
        verify(productRepository).findByCategory("Especialidades");
    }

    @Test
    void getById_shouldReturnProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductDTOResponse result = productService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Producto no encontrado con id 99");
    }

    @Test
    void create_shouldSaveAndReturnMappedProduct() {
        ProductDTORequest request = ProductDTORequest.builder()
                .name("Fabada Asturiana")
                .description("La receta de siempre.")
                .price(new BigDecimal("16.00"))
                .image("/menu-img/fabada.png")
                .category("Especialidades")
                .build();

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductDTOResponse result = productService.create(request);

        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }

    @Test
    void update_shouldModifyAndSaveProduct_whenExists() {
        ProductDTORequest request = ProductDTORequest.builder()
                .name("Fabada Actualizada")
                .description("Nueva descripción.")
                .price(new BigDecimal("17.00"))
                .image("/menu-img/fabada2.png")
                .category("Especialidades")
                .available(true)
                .featured(false)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductDTOResponse result = productService.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(product.getName()).isEqualTo("Fabada Actualizada");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("17.00"));
    }

    @Test
    void update_shouldThrowNotFound_whenProductDoesNotExist() {
        ProductDTORequest request = ProductDTORequest.builder().name("X").build();

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Producto no encontrado con id 99");
    }

    @Test
    void delete_shouldRemoveProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void delete_shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Producto no encontrado con id 99");
    }

}