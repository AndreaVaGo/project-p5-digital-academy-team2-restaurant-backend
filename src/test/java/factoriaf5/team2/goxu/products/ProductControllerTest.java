package factoriaf5.team2.goxu.products;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.products.dtos.ProductDTORequest;
import factoriaf5.team2.goxu.products.dtos.ProductDTOResponse;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService service;

    @InjectMocks
    private ProductController controller;

    private ProductDTOResponse response;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        response = ProductDTOResponse.builder()
                .id(1L)
                .name("Fabada Asturiana")
                .category("Especialidades")
                .price(new BigDecimal("16.00"))
                .build();
    }

    @Test
    void getProducts_shouldReturnFeatured_whenFeaturedTrue() {
        when(service.getFeatured()).thenReturn(List.of(response));

        ResponseEntity<List<ProductDTOResponse>> result = controller.getProducts(null, true);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        verify(service).getFeatured();
    }

    @Test
    void getProducts_shouldReturnByCategory_whenCategoryGiven() {
        when(service.getByCategory("Especialidades")).thenReturn(List.of(response));

        ResponseEntity<List<ProductDTOResponse>> result = controller.getProducts("Especialidades", null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        verify(service).getByCategory("Especialidades");
    }

    @Test
    void getProducts_shouldReturnAll_whenNoFiltersGiven() {
        when(service.getAll()).thenReturn(List.of(response));

        ResponseEntity<List<ProductDTOResponse>> result = controller.getProducts(null, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
        verify(service).getAll();
    }

    @Test
    void getProductById_shouldReturnProductFromService() {
        when(service.getById(1L)).thenReturn(response);

        ResponseEntity<ProductDTOResponse> result = controller.getProductById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void createProduct_shouldReturnCreatedStatus() {
        ProductDTORequest request = ProductDTORequest.builder()
                .name("Fabada Asturiana")
                .description("La receta de siempre.")
                .price(new BigDecimal("16.00"))
                .image("/menu-img/fabada.png")
                .category("Especialidades")
                .build();

        when(service.create(request)).thenReturn(response);

        ResponseEntity<ProductDTOResponse> result = controller.createProduct(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateProduct_shouldReturnOkWithUpdatedProduct() {
        ProductDTORequest request = ProductDTORequest.builder()
                .name("Fabada Actualizada")
                .description("Nueva descripción.")
                .price(new BigDecimal("17.00"))
                .image("/menu-img/fabada2.png")
                .category("Especialidades")
                .build();

        when(service.update(1L, request)).thenReturn(response);

        ResponseEntity<ProductDTOResponse> result = controller.updateProduct(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteProduct_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteProduct(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

}