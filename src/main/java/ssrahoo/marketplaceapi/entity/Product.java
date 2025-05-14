package ssrahoo.marketplaceapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;

    @ManyToOne
    @JoinColumn(name="seller_id")
    private Seller seller;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    private String name;
    private Double unitPrice;
    private Integer stock;

    @CreationTimestamp
    private Instant created;

    @UpdateTimestamp
    private Instant modified;

    public Product() {
    }

    public Product(UUID productId, Seller seller, String name, Double unitPrice, Integer stock, Instant created, Instant modified) {
        this.productId = productId;
        this.seller = seller;
        this.created = created;
        this.modified = modified;
        this.reviews = new ArrayList<>();
        this.name = name;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }


    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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
