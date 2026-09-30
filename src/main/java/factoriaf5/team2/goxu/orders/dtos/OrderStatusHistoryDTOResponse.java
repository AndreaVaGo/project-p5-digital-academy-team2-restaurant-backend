package factoriaf5.team2.goxu.orders.dtos;

import java.time.LocalDateTime;

import factoriaf5.team2.goxu.orders.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderStatusHistoryDTOResponse {

    private OrderStatus status;
    private LocalDateTime changedAt;

}