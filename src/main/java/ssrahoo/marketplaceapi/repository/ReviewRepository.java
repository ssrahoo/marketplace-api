package ssrahoo.marketplaceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssrahoo.marketplaceapi.entity.Review;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
