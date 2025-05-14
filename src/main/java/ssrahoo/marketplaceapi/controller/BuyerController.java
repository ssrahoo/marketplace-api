package ssrahoo.marketplaceapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssrahoo.marketplaceapi.dto.ReviewRegistrationDto;
import ssrahoo.marketplaceapi.dto.ReviewResponseDto;
import ssrahoo.marketplaceapi.dto.TransactionRegistrationDto;
import ssrahoo.marketplaceapi.dto.TransactionResponseDto;
import ssrahoo.marketplaceapi.service.BuyerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("buyer")
public class BuyerController {
    private BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping("/{buyerId}/product")
    public ResponseEntity<Void> saveTransaction(
            @PathVariable("buyerId") UUID buyerId,
            @RequestBody TransactionRegistrationDto transactionRegistrationDto
        ){
        boolean ok = buyerService.saveTransaction(buyerId, transactionRegistrationDto);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{buyerId}/product")
    public ResponseEntity<List<TransactionResponseDto>> findAllTransactions(
            @PathVariable("buyerId") UUID buyerId
    ){
        var transactions = buyerService.findAllTransactions(buyerId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{buyerId}/product/{productId}/review")
    public ResponseEntity<Void> saveReview(
            @PathVariable("buyerId") UUID buyerId,
            @PathVariable("productId") UUID productId,
            @RequestBody ReviewRegistrationDto reviewRegistrationDto
    ){
        buyerService.saveReview(buyerId, productId, reviewRegistrationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{buyerId}/product/{productId}/review")
    public ResponseEntity<List<ReviewResponseDto>> findAllReviews(
            @PathVariable("buyerId") UUID buyerId,
            @PathVariable("productId") UUID productId
    ){
        var reviews = buyerService.findAllReviews(buyerId, productId);
        return ResponseEntity.ok(reviews);
    }

}
