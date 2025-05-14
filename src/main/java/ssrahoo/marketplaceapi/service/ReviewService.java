package ssrahoo.marketplaceapi.service;

import org.springframework.stereotype.Service;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.dto.ReviewResponseDto;
import ssrahoo.marketplaceapi.dto.ReviewUpdateDto;
import ssrahoo.marketplaceapi.entity.Review;
import ssrahoo.marketplaceapi.repository.ReviewRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // read all
    public List<ReviewResponseDto> findAll(){
        return reviewRepository.findAll()
                .stream()
                .map(r ->
                        new ReviewResponseDto(
                                r.getReviewId(),
                                r.getRating(),
                                r.getComment()
                        ))
                .toList();
    }

    // read by id
    public Optional<ReviewResponseDto> findById(UUID id){
        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            return Optional.of(new ReviewResponseDto(
                    review.get().getReviewId(),
                    review.get().getRating(),
                    review.get().getComment()
            ));
        }else{
            return Optional.empty();
        }

    }

    // update
    public void updateById(UUID id, ReviewUpdateDto reviewUpdateDto){
        var result = reviewRepository.findById(id);

        if (result.isPresent()) {
            var review = result.get();

            if(reviewUpdateDto.rating() != null){
                review.setRating(reviewUpdateDto.rating());
            }

            if(reviewUpdateDto.comment() != null){
                review.setComment(reviewUpdateDto.comment());
            }

            review.setModified(Instant.now());
            reviewRepository.save(review);
        }
    }

    // delete
    public void deleteById(UUID id){
        if(reviewRepository.existsById(id)){
            reviewRepository.deleteById(id);
        }
    }

}
