package factoriaf5.team2.goxu.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;
import factoriaf5.team2.goxu.profile.dtos.CustomerProfileUpdateDTORequest;
import factoriaf5.team2.goxu.profile.CustomerProfileEntity;
import factoriaf5.team2.goxu.profile.CustomerProfileRepository;

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
                                .surname("García")
                                .phone("600123456")
                                .address("Calle Uría 45")
                                .postalCode("33003")
                                .city("Oviedo")
                                .avatar("https://example.com/avatar.jpg")
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

                when(userRepository.findById(1L))
                                .thenReturn(Optional.of(user));

                when(customerProfileRepository.findByUserId(1L))
                                .thenReturn(Optional.of(profile));

                when(orderRepository.findByUserId(1L))
                                .thenReturn(List.of(order));

                when(orderMapper.toResponse(order))
                                .thenReturn(orderResponse);

                CustomerProfileDTOResponse result = customerProfileService.getProfile(1L);

                assertThat(result.getName()).isEqualTo("Cliente Test");
                assertThat(result.getEmail()).isEqualTo("cliente@test.com");
                assertThat(result.getTotalOrders()).isEqualTo(1);
                assertThat(result.getTotalSpent())
                                .isEqualTo(new BigDecimal("29.00"));
                assertThat(result.getFavoriteProducts()).hasSize(1);
                assertThat(result.getFavoriteProducts().get(0).getProductName())
                                .isEqualTo("Fabada Asturiana");
                assertThat(result.getRecentOrders()).hasSize(1);
        }

        @Test
        void getProfile_shouldReturnPersonalData() {
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

                when(userRepository.findById(1L))
                                .thenReturn(Optional.of(user));

                when(customerProfileRepository.findByUserId(1L))
                                .thenReturn(Optional.of(profile));

                when(orderRepository.findByUserId(1L))
                                .thenReturn(List.of());

                CustomerProfileDTOResponse result = customerProfileService.getProfile(1L);

                assertThat(result.getName()).isEqualTo("Cliente Test");
                assertThat(result.getEmail()).isEqualTo("cliente@test.com");
                assertThat(result.getSurname()).isEqualTo("García");
                assertThat(result.getPhone()).isEqualTo("600123456");
                assertThat(result.getAddress()).isEqualTo("Calle Uría 45");
                assertThat(result.getPostalCode()).isEqualTo("33003");
                assertThat(result.getCity()).isEqualTo("Oviedo");
                assertThat(result.getAvatar())
                                .isEqualTo("https://example.com/avatar.jpg");
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
                                .surname("García")
                                .phone("600123456")
                                .address("Calle Uría 45")
                                .postalCode("33003")
                                .city("Oviedo")
                                .avatar("https://example.com/avatar.jpg")
                                .build();

                when(userRepository.findById(2L))
                                .thenReturn(Optional.of(user));

                when(customerProfileRepository.findByUserId(2L))
                                .thenReturn(Optional.of(profile));

                when(orderRepository.findByUserId(2L))
                                .thenReturn(List.of());

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
