package ssrahoo.marketplaceapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Transaction {

    @EmbeddedId
    private TransactionId id;

    @ManyToOne
    @MapsId("buyerId")
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer amount;
    private BigDecimal totalPrice;


    @CreationTimestamp
    private Instant created;

    @UpdateTimestamp
    private Instant modified;

    public Transaction() {
    }

    public Transaction(TransactionId id, Buyer buyer, Product product, Integer amount, BigDecimal totalPrice, Instant created, Instant modified) {
        this.id = id;
        this.buyer = buyer;
        this.product = product;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.created = created;
        this.modified = modified;
    }



    public TransactionId getId() {
        return id;
    }

    public void setId(TransactionId id) {
        this.id = id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }
}
