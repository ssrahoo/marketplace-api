package ssrahoo.marketplaceapi.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Buyer {
    // Shares primary key with User

    @Id
    @Column(name="buyer_id")
    private UUID buyerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "buyer")
    private List<Transaction> transactionList;

    @OneToMany(mappedBy = "buyer")
    private List<Review> reviews;

    public Buyer() {
    }

    public Buyer(User user) {
        this.user = user;
        this.transactionList = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getBuyerProductList() {
        return transactionList;
    }

    public void setBuyerProductList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
