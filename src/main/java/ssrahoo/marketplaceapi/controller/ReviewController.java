package ssrahoo.marketplaceapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssrahoo.marketplaceapi.dto.ReviewResponseDto;
import ssrahoo.marketplaceapi.dto.ReviewUpdateDto;
import ssrahoo.marketplaceapi.entity.Review;
import ssrahoo.marketplaceapi.service.ReviewService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST is handled by BuyerController

    // read all
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> findAll() {
        var reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    //TODO: endpoint that gets all the reviews made for a specific product

    // TODO: endpoint that gets all the reviews created by a specific buyer

    // read by id
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> findById(@PathVariable("id") UUID id) {
        var review = reviewService.findById(id);
        return review.isPresent() ? ResponseEntity.ok(review.get()) : ResponseEntity.notFound().build();
    }


    // update
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(
            @PathVariable("id") UUID id,
            @RequestBody ReviewUpdateDto reviewUpdateDto
    ){
        reviewService.updateById(id, reviewUpdateDto);
        return ResponseEntity.noContent().build();
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id){
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
