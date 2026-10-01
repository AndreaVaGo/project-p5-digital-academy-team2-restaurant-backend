package factoriaf5.team2.goxu.orders;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderItemDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderStatusHistoryDTOResponse;
import factoriaf5.team2.goxu.products.ProductEntity;

@Component
public class OrderMapper {

    public OrderItemEntity toEntity(OrderItemDTORequest dto, ProductEntity product) {
        return OrderItemEntity.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(product.getPrice())
                .build();
    }

    public OrderItemDTOResponse toResponse(OrderItemEntity entity) {
        BigDecimal subtotal = entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));

        return OrderItemDTOResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }

    public OrderDTOResponse toResponse(OrderEntity entity) {
        List<OrderItemDTOResponse> itemResponses = entity.getItems().stream()
                .map(this::toResponse)
                .toList();

        return OrderDTOResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .tableNumber(entity.getTableNumber())
                .status(entity.getStatus())
                .paid(entity.isPaid())
                .total(entity.getTotal())
                .createdAt(entity.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    public OrderStatusHistoryDTOResponse toResponse(OrderStatusHistoryEntity entity) {
        return OrderStatusHistoryDTOResponse.builder()
                .status(entity.getStatus())
                .changedAt(entity.getChangedAt())
                .build();
    }

}