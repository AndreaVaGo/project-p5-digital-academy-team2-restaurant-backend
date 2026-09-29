package factoriaf5.team2.goxu.orders;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {

    List<OrderStatusHistoryEntity> findByOrderIdOrderByChangedAtAsc(Long orderId);

}