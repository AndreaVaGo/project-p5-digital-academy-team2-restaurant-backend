package factoriaf5.team2.goxu.profile;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.dashboard.dtos.TopProductDTO;
import factoriaf5.team2.goxu.orders.OrderEntity;
import factoriaf5.team2.goxu.orders.OrderItemEntity;
import factoriaf5.team2.goxu.orders.OrderMapper;
import factoriaf5.team2.goxu.orders.OrderRepository;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.products.ProductEntity;
import factoriaf5.team2.goxu.profile.dtos.CustomerProfileDTOResponse;
import factoriaf5.team2.goxu.profile.dtos.CustomerProfileUpdateDTORequest;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

        private static final int FAVORITE_PRODUCTS_LIMIT = 3;
        private static final int RECENT_ORDERS_LIMIT = 5;

        private final UserRepository userRepository;
        private final OrderRepository orderRepository;
        private final OrderMapper orderMapper;
        private final CustomerProfileRepository customerProfileRepository;

        @Transactional(readOnly = true)
        public CustomerProfileDTOResponse getProfile(Long userId) {
                UserEntity user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + userId));

                CustomerProfileEntity profile = customerProfileRepository
                                .findByUserId(userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Perfil no encontrado"));

                List<OrderEntity> orders = orderRepository.findByUserId(userId);

                BigDecimal totalSpent = orders.stream()
                                .map(OrderEntity::getTotal)
                                .filter(total -> total != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<OrderDTOResponse> recentOrders = orders.stream()
                                .sorted(Comparator.comparing(OrderEntity::getCreatedAt,
                                                Comparator.nullsLast(Comparator.reverseOrder())))
                                .limit(RECENT_ORDERS_LIMIT)
                                .map(orderMapper::toResponse)
                                .toList();

                List<TopProductDTO> favoriteProducts = favoriteProducts(orders);

                return CustomerProfileDTOResponse.builder()
                                .userId(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .totalOrders(orders.size())
                                .totalSpent(totalSpent)
                                .favoriteProducts(favoriteProducts)
                                .recentOrders(recentOrders)
                                .surname(profile.getSurname())
                                .phone(profile.getPhone())
                                .address(profile.getAddress())
                                .postalCode(profile.getPostalCode())
                                .city(profile.getCity())
                                .avatar(profile.getAvatar())
                                .build();
        }

        private List<TopProductDTO> favoriteProducts(List<OrderEntity> orders) {
                List<OrderItemEntity> items = orders.stream()
                                .flatMap(order -> order.getItems().stream())
                                .toList();

                Map<Long, Long> unitsByProductId = items.stream()
                                .collect(Collectors.groupingBy(
                                                item -> item.getProduct().getId(),
                                                Collectors.summingLong(OrderItemEntity::getQuantity)));

                Map<Long, String> productNamesById = items.stream()
                                .map(OrderItemEntity::getProduct)
                                .collect(Collectors.toMap(
                                                ProductEntity::getId,
                                                ProductEntity::getName,
                                                (existingName, newName) -> existingName));

                return unitsByProductId.entrySet().stream()
                                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                                .limit(FAVORITE_PRODUCTS_LIMIT)
                                .map(entry -> TopProductDTO.builder()
                                                .productId(entry.getKey())
                                                .productName(productNamesById.get(entry.getKey()))
                                                .unitsSold(entry.getValue())
                                                .build())
                                .toList();
        }

        public CustomerProfileDTOResponse updateProfile(
                        Long userId,
                        CustomerProfileUpdateDTORequest request) {

                CustomerProfileEntity profile = customerProfileRepository
                                .findByUserId(userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Perfil no encontrado"));

                profile.setSurname(request.surname());
                profile.setPhone(request.phone());
                profile.setAddress(request.address());
                profile.setPostalCode(request.postalCode());
                profile.setCity(request.city());
                profile.setAvatar(request.avatar());

                CustomerProfileEntity savedProfile = customerProfileRepository.save(profile);

                UserEntity user = savedProfile.getUser();

                return CustomerProfileDTOResponse.builder()
                                .userId(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .surname(savedProfile.getSurname())
                                .phone(savedProfile.getPhone())
                                .address(savedProfile.getAddress())
                                .postalCode(savedProfile.getPostalCode())
                                .city(savedProfile.getCity())
                                .avatar(savedProfile.getAvatar())
                                .build();
        }
}
