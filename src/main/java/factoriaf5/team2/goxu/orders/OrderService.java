package factoriaf5.team2.goxu.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import factoriaf5.team2.goxu.orders.dtos.OrderDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderStatusHistoryDTOResponse;
import factoriaf5.team2.goxu.products.ProductEntity;
import factoriaf5.team2.goxu.products.ProductRepository;
import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderNotificationService notificationService;
    private final OrderStatusHistoryRepository historyRepository;

    public List<OrderDTOResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderDTOResponse> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderDTOResponse> getByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderDTOResponse getById(Long id) {
        OrderEntity order = findOrderOrThrow(id);
        return orderMapper.toResponse(order);
    }

    public List<OrderStatusHistoryDTOResponse> getTracking(Long id) {
        OrderEntity order = findOrderOrThrow(id);
        return historyRepository.findByOrderIdOrderByChangedAtAsc(order.getId())
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderDTOResponse create(OrderDTORequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + request.getUserId()));

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .tableNumber(request.getTableNumber())
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDTORequest itemRequest : request.getItems()) {
            ProductEntity product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Producto no encontrado con id " + itemRequest.getProductId()));

            OrderItemEntity item = orderMapper.toEntity(itemRequest, product);
            item.setOrder(order);
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotal(total);

        OrderEntity saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    public OrderDTOResponse updateStatus(Long id, OrderStatus status) {
        OrderEntity order = findOrderOrThrow(id);
        order.setStatus(status);
        OrderEntity updated = orderRepository.save(order);

        historyRepository.save(OrderStatusHistoryEntity.builder()
                .order(order)
                .status(status)
                .changedAt(LocalDateTime.now())
                .build());

        if (status == OrderStatus.ON_THE_WAY) {
            notificationService.sendOrderOnTheWay(order);
        } else if (status == OrderStatus.DELIVERED) {
            notificationService.sendOrderDelivered(order);
        }

        return orderMapper.toResponse(updated);
    }

    public OrderDTOResponse markAsPaid(Long id) {
        OrderEntity order = findOrderOrThrow(id);
        order.setPaid(true);
        OrderEntity updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    public void delete(Long id) {
        OrderEntity order = findOrderOrThrow(id);
        orderRepository.delete(order);
    }

    private OrderEntity findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido no encontrado con id " + id));
    }

}