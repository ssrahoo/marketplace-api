package ssrahoo.marketplaceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssrahoo.marketplaceapi.entity.Seller;

import java.util.UUID;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {}
