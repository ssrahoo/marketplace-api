package ssrahoo.marketplaceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssrahoo.marketplaceapi.entity.Transaction;
import ssrahoo.marketplaceapi.entity.TransactionId;

public interface BuyerProductRepository extends JpaRepository<Transaction, TransactionId> {}
