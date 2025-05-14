package ssrahoo.marketplaceapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

// TODO: limit the number of reviews that a buyer can make to any given product to 1

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="review_id")
    private UUID reviewId;

    @ManyToOne
    @JoinColumn(name="buyer_id")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private Integer rating;
    private String comment;

    @CreationTimestamp
    private Instant created;

    @UpdateTimestamp
    private Instant modified;

    public Review() {
    }

    public Review(UUID reviewId, Buyer buyer, Product product, Integer rating, String comment, Instant created, Instant modified) {
        this.reviewId = reviewId;
        this.buyer = buyer;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
        this.created = created;
        this.modified = modified;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
