package ssrahoo.marketplaceapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssrahoo.marketplaceapi.dto.ReviewRegistrationDto;
import ssrahoo.marketplaceapi.dto.ReviewResponseDto;
import ssrahoo.marketplaceapi.dto.TransactionRegistrationDto;
import ssrahoo.marketplaceapi.dto.TransactionResponseDto;
import ssrahoo.marketplaceapi.entity.*;
import ssrahoo.marketplaceapi.repository.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BuyerService {

    private BuyerRepository buyerRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private BuyerProductRepository buyerProductRepository;
    private ReviewRepository reviewRepository;


    public BuyerService(BuyerRepository buyerRepository, UserRepository userRepository, ProductRepository productRepository, BuyerProductRepository buyerProductRepository, ReviewRepository reviewRepository) {
        this.buyerRepository = buyerRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.buyerProductRepository = buyerProductRepository;
        this.reviewRepository = reviewRepository;
    }

    public boolean saveTransaction(UUID buyerId, TransactionRegistrationDto transactionRegistrationDto) {
        UUID productId = UUID.fromString(transactionRegistrationDto.productId());
        Integer amount = transactionRegistrationDto.amount();

        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        double totalPrice = amount * product.getUnitPrice();

        //check if transaction is doable (i.e., buyer having enough funds and stock >= amount)
        if (buyer.getUser().getWallet() < totalPrice)
            return false;

        if (product.getStock() < amount || amount < 1)
            return false;

        // update buyer and seller wallets
        User buyerUser = buyer.getUser();
        buyerUser.setWallet(buyerUser.getWallet() - totalPrice);

        User sellerUser = product.getSeller().getUser();
        sellerUser.setWallet(sellerUser.getWallet() + totalPrice);

        // update product stock
        product.setStock(product.getStock() - amount);

        // create transaction
        TransactionId id = new TransactionId(buyer.getBuyerId(), product.getProductId());
        Transaction transaction = new Transaction(
                id,
                buyer,
                product,
                amount,
                totalPrice,
                Instant.now(),
                null
        );

        // save
        userRepository.save(buyerUser);
        userRepository.save(sellerUser);
        productRepository.save(product);
        buyerProductRepository.save(transaction);

        return true;
    }

    public List<TransactionResponseDto> findAllTransactions(UUID buyerId) {
        var buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return buyer.getBuyerProductList()
                .stream()
                .map(t -> new TransactionResponseDto(
                        t.getBuyer().getUser().getUsername(),
                        t.getBuyer().getBuyerId(),
                        t.getProduct().getName(),
                        t.getProduct().getProductId(),
                        t.getAmount(),
                        t.getTotalPrice(),
                        t.getCreated(),
                        t.getModified()
                )).toList();
    }

    public void saveReview(UUID buyerId, UUID productId, ReviewRegistrationDto reviewRegistrationDto) {
        var buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Review review = new Review(
                null,
                buyer,
                product,
                reviewRegistrationDto.rating(),
                reviewRegistrationDto.comment(),
                Instant.now(),
                null
        );
        var id = reviewRepository.save(review).getReviewId();
    }

    public List<ReviewResponseDto> findAllReviews(UUID buyerId, UUID productId) {
        var buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var idsOfReviewsMadeByBuyer = buyer.getReviews()
                .stream()
                .map(review -> review.getReviewId())
                .collect(Collectors.toList());

        var reviewsMadeByBuyerForProduct = product.getReviews()
                .stream()
                .filter(review -> idsOfReviewsMadeByBuyer.contains(review.getReviewId()))
                .collect(Collectors.toList());

        return reviewsMadeByBuyerForProduct
                .stream()
                .map(r ->
                        new ReviewResponseDto(
                                r.getReviewId(),
                                r.getRating(),
                                r.getComment()
                        )
                )
                .toList();
    }


}
