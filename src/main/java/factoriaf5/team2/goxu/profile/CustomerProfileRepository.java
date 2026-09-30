package factoriaf5.team2.goxu.profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfileEntity, Long> {

    Optional<CustomerProfileEntity> findByUserId(Long userId);
}
