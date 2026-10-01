package factoriaf5.team2.goxu.orders;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import factoriaf5.team2.goxu.users.UserEntity;
import factoriaf5.team2.goxu.users.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderStatusHistoryRepositoryTest {

    @TestConfiguration(proxyBeanMethods = false)
    static class ContainerConfig {

        @Bean
        @ServiceConnection
        MySQLContainer mysqlContainer() {
            return new MySQLContainer(DockerImageName.parse("mysql:latest"));
        }
    }

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByOrderIdOrderByChangedAtAsc_shouldReturnOnlyThatOrdersHistory_inChronologicalOrder() {
        UserEntity user = userRepository.save(UserEntity.builder()
                .name("Andrea")
                .email("andrea@example.com")
                .password("hashed-password")
                .build());

        OrderEntity orderOne = orderRepository.save(OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .build());

        OrderEntity orderTwo = orderRepository.save(OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .build());

        LocalDateTime now = LocalDateTime.now();

        historyRepository.save(OrderStatusHistoryEntity.builder()
                .order(orderOne)
                .status(OrderStatus.IN_KITCHEN)
                .changedAt(now.minusMinutes(10))
                .build());

        historyRepository.save(OrderStatusHistoryEntity.builder()
                .order(orderOne)
                .status(OrderStatus.ON_THE_WAY)
                .changedAt(now)
                .build());

        historyRepository.save(OrderStatusHistoryEntity.builder()
                .order(orderTwo)
                .status(OrderStatus.IN_KITCHEN)
                .changedAt(now)
                .build());

        List<OrderStatusHistoryEntity> result = historyRepository.findByOrderIdOrderByChangedAtAsc(orderOne.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.IN_KITCHEN);
        assertThat(result.get(1).getStatus()).isEqualTo(OrderStatus.ON_THE_WAY);
    }

}