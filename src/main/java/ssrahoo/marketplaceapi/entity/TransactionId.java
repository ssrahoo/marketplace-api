package ssrahoo.marketplaceapi.entity;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * TransactionId is composed of three IDs: transactionId, buyerId
 * and productId. The IDs buyerId and productId define the
 * N x N relationship between buyer and product. The transactionId
 * uniquely identifies the transaction itself, ensuring that
 * new transaction entries do not replace an older entry
 * in the database when the pair (transaction.buyer_id,
 * transaction.product_id) is kept the same.
 */
@Embeddable
public class TransactionId {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "buyer_id")
    private UUID buyerId;

    @Column(name = "product_id")
    private UUID productId;

    public TransactionId() {
    }

    public TransactionId(UUID buyerId, UUID productId) {
        this.buyerId = buyerId;
        this.productId = productId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
