package factoriaf5.team2.goxu.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.orders.OrderEntity;
import factoriaf5.team2.goxu.orders.OrderItemEntity;
import factoriaf5.team2.goxu.orders.OrderMapper;
import factoriaf5.team2.goxu.orders.OrderRepository;
import factoriaf5.team2.goxu.orders.OrderStatus;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.products.ProductEntity;
import factoriaf5.team2.goxu.profile.dtos.CustomerProfileDTOResponse;
import factoriaf5.team2.goxu.profile.dtos.CustomerProfileUpdateDTORequest;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private OrderMapper orderMapper;

        @Mock
        private CustomerProfileRepository customerProfileRepository;

        @InjectMocks
        private CustomerProfileService customerProfileService;

        @Test
        void getProfile_shouldReturnStatsAndFavoriteProducts_whenUserHasOrders() {
                UserEntity user = UserEntity.builder()
                                .id(1L)
                                .name("Cliente Test")
                                .email("cliente@test.com")
                                .build();

                CustomerProfileEntity profile = CustomerProfileEntity.builder()
                                .id(1L)
                                .user(user)
                                .surname("Test")
                                .phone("600000000")
                                .address("Calle Test")
                                .postalCode("33000")
                                .city("Oviedo")
                                .avatar(null)
                                .build();

                ProductEntity fabada = ProductEntity.builder()
                                .id(1L)
                                .name("Fabada Asturiana")
                                .price(new BigDecimal("14.50"))
                                .build();

                OrderItemEntity item = OrderItemEntity.builder()
                                .product(fabada)
                                .quantity(2)
                                .unitPrice(fabada.getPrice())
                                .build();

                OrderEntity order = OrderEntity.builder()
                                .id(1L)
                                .user(user)
                                .status(OrderStatus.DELIVERED)
                                .total(new BigDecimal("29.00"))
                                .createdAt(LocalDateTime.of(2026, 9, 10, 13, 0))
                                .items(List.of(item))
                                .build();

                OrderDTOResponse orderResponse = OrderDTOResponse.builder()
                                .id(1L)
                                .userId(1L)
                                .userName("Cliente Test")
                                .status(OrderStatus.DELIVERED)
                                .total(new BigDecimal("29.00"))
                                .createdAt(order.getCreatedAt())
                                .items(List.of())
                                .build();

                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(customerProfileRepository.findByUserId(1L))
                                .thenReturn(Optional.of(profile));
                when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                CustomerProfileDTOResponse result = customerProfileService.getProfile(1L);

                assertThat(result.getName()).isEqualTo("Cliente Test");
                assertThat(result.getEmail()).isEqualTo("cliente@test.com");
                assertThat(result.getTotalOrders()).isEqualTo(1);
                assertThat(result.getTotalSpent()).isEqualTo(new BigDecimal("29.00"));
                assertThat(result.getFavoriteProducts()).hasSize(1);
                assertThat(result.getFavoriteProducts().get(0).getProductName())
                                .isEqualTo("Fabada Asturiana");
                assertThat(result.getRecentOrders()).hasSize(1);
        }

        @Test
        void getProfile_shouldReturnZeroStats_whenUserHasNoOrders() {
                UserEntity user = UserEntity.builder()
                                .id(2L)
                                .name("Sin Pedidos")
                                .email("sin@test.com")
                                .build();

                CustomerProfileEntity profile = CustomerProfileEntity.builder()
                                .id(2L)
                                .user(user)
                                .build();

                when(userRepository.findById(2L)).thenReturn(Optional.of(user));
                when(customerProfileRepository.findByUserId(2L))
                                .thenReturn(Optional.of(profile));
                when(orderRepository.findByUserId(2L)).thenReturn(List.of());

                CustomerProfileDTOResponse result = customerProfileService.getProfile(2L);

                assertThat(result.getTotalOrders()).isEqualTo(0);
                assertThat(result.getTotalSpent()).isEqualTo(BigDecimal.ZERO);
                assertThat(result.getFavoriteProducts()).isEmpty();
                assertThat(result.getRecentOrders()).isEmpty();
        }

        @Test
        void getProfile_shouldThrow_whenUserNotFound() {
                when(userRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> customerProfileService.getProfile(99L))
                                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        void getProfile_shouldThrow_whenProfileNotFound() {
                UserEntity user = UserEntity.builder()
                                .id(3L)
                                .name("Sin Perfil")
                                .email("sinperfil@test.com")
                                .build();

                when(userRepository.findById(3L)).thenReturn(Optional.of(user));
                when(customerProfileRepository.findByUserId(3L))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> customerProfileService.getProfile(3L))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Perfil no encontrado");
        }

        @Test
        void getProfile_shouldIgnoreOrdersWithNullTotal() {
                UserEntity user = UserEntity.builder()
                                .id(4L)
                                .name("Cliente Sin Total")
                                .email("sintotal@test.com")
                                .build();

                CustomerProfileEntity profile = CustomerProfileEntity.builder()
                                .id(4L)
                                .user(user)
                                .build();

                OrderEntity order = OrderEntity.builder()
                                .id(4L)
                                .user(user)
                                .status(OrderStatus.DELIVERED)
                                .total(null)
                                .createdAt(LocalDateTime.of(2026, 9, 20, 13, 0))
                                .items(List.of())
                                .build();

                OrderDTOResponse orderResponse = OrderDTOResponse.builder()
                                .id(4L)
                                .userId(4L)
                                .userName("Cliente Sin Total")
                                .status(OrderStatus.DELIVERED)
                                .total(null)
                                .createdAt(order.getCreatedAt())
                                .items(List.of())
                                .build();

                when(userRepository.findById(4L)).thenReturn(Optional.of(user));
                when(customerProfileRepository.findByUserId(4L))
                                .thenReturn(Optional.of(profile));
                when(orderRepository.findByUserId(4L)).thenReturn(List.of(order));
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                CustomerProfileDTOResponse result = customerProfileService.getProfile(4L);

                assertThat(result.getTotalOrders()).isEqualTo(1);
                assertThat(result.getTotalSpent()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        void getProfile_shouldLimitFavoriteProductsToThree() {
                UserEntity user = UserEntity.builder()
                                .id(5L)
                                .name("Cliente Favoritos")
                                .email("favoritos@test.com")
                                .build();

                CustomerProfileEntity profile = CustomerProfileEntity.builder()
                                .id(5L)
                                .user(user)
                                .build();

                ProductEntity product1 = ProductEntity.builder()
                                .id(1L)
                                .name("Fabada")
                                .price(new BigDecimal("14.00"))
                                .build();

                ProductEntity product2 = ProductEntity.builder()
                                .id(2L)
                                .name("Cachopo")
                                .price(new BigDecimal("18.00"))
                                .build();

                ProductEntity product3 = ProductEntity.builder()
                                .id(3L)
                                .name("Tortos")
                                .price(new BigDecimal("10.00"))
                                .build();

                ProductEntity product4 = ProductEntity.builder()
                                .id(4L)
                                .name("Frixuelos")
                                .price(new BigDecimal("8.00"))
                                .build();

                OrderItemEntity item1 = OrderItemEntity.builder()
                                .product(product1)
                                .quantity(4)
                                .unitPrice(product1.getPrice())
                                .build();

                OrderItemEntity item2 = OrderItemEntity.builder()
                                .product(product2)
                                .quantity(3)
                                .unitPrice(product2.getPrice())
                                .build();

                OrderItemEntity item3 = OrderItemEntity.builder()
                                .product(product3)
                                .quantity(2)
                                .unitPrice(product3.getPrice())
                                .build();

                OrderItemEntity item4 = OrderItemEntity.builder()
                                .product(product4)
                                .quantity(1)
                                .unitPrice(product4.getPrice())
                                .build();

                OrderEntity order = OrderEntity.builder()
                                .id(5L)
                                .user(user)
                                .status(OrderStatus.DELIVERED)
                                .total(new BigDecimal("100.00"))
                                .createdAt(LocalDateTime.of(2026, 9, 20, 13, 0))
                                .items(List.of(item1, item2, item3, item4))
                                .build();

                OrderDTOResponse orderResponse = OrderDTOResponse.builder()
                                .id(5L)
                                .userId(5L)
                                .userName("Cliente Favoritos")
                                .status(OrderStatus.DELIVERED)
                                .total(new BigDecimal("100.00"))
                                .createdAt(order.getCreatedAt())
                                .items(List.of())
                                .build();

                when(userRepository.findById(5L)).thenReturn(Optional.of(user));
                when(customerProfileRepository.findByUserId(5L))
                                .thenReturn(Optional.of(profile));
                when(orderRepository.findByUserId(5L)).thenReturn(List.of(order));
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                CustomerProfileDTOResponse result = customerProfileService.getProfile(5L);

                assertThat(result.getFavoriteProducts()).hasSize(3);
                assertThat(result.getFavoriteProducts().get(0).getProductName())
                                .isEqualTo("Fabada");
                assertThat(result.getFavoriteProducts().get(1).getProductName())
                                .isEqualTo("Cachopo");
                assertThat(result.getFavoriteProducts().get(2).getProductName())
                                .isEqualTo("Tortos");
        }

        @Test
        void updateProfile_shouldThrow_whenProfileNotFound() {
                CustomerProfileUpdateDTORequest request = new CustomerProfileUpdateDTORequest(
                                "López",
                                "611987654",
                                "Calle Corrida 10",
                                "33206",
                                "Gijón",
                                "https://example.com/avatar.jpg");

                when(customerProfileRepository.findByUserId(99L))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> customerProfileService.updateProfile(99L, request))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Perfil no encontrado");
        }

        @Test
        void updateProfile_shouldUpdatePersonalData() {
                UserEntity user = UserEntity.builder()
                                .id(1L)
                                .name("Cliente Test")
                                .email("cliente@test.com")
                                .build();

                CustomerProfileEntity profile = CustomerProfileEntity.builder()
                                .id(1L)
                                .user(user)
                                .surname("García")
                                .phone("600123456")
                                .address("Calle Uría 45")
                                .postalCode("33003")
                                .city("Oviedo")
                                .avatar("https://example.com/avatar.jpg")
                                .build();

                CustomerProfileUpdateDTORequest request = new CustomerProfileUpdateDTORequest(
                                "López",
                                "611987654",
                                "Calle Corrida 10",
                                "33206",
                                "Gijón",
                                "https://example.com/new-avatar.jpg");

                when(customerProfileRepository.findByUserId(1L))
                                .thenReturn(Optional.of(profile));

                when(customerProfileRepository.save(profile))
                                .thenReturn(profile);

                CustomerProfileDTOResponse result = customerProfileService.updateProfile(1L, request);

                assertThat(result.getUserId()).isEqualTo(1L);
                assertThat(result.getName()).isEqualTo("Cliente Test");
                assertThat(result.getEmail()).isEqualTo("cliente@test.com");
                assertThat(result.getSurname()).isEqualTo("López");
                assertThat(result.getPhone()).isEqualTo("611987654");
                assertThat(result.getAddress()).isEqualTo("Calle Corrida 10");
                assertThat(result.getPostalCode()).isEqualTo("33206");
                assertThat(result.getCity()).isEqualTo("Gijón");
                assertThat(result.getAvatar())
                                .isEqualTo("https://example.com/new-avatar.jpg");

                verify(customerProfileRepository).save(profile);
        }
}
