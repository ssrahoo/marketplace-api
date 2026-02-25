package ssrahoo.marketplaceapi.service;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssrahoo.marketplaceapi.dto.ReviewResponseDto;
import ssrahoo.marketplaceapi.dto.ReviewUpdateDto;
import ssrahoo.marketplaceapi.entity.Buyer;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.entity.Review;
import ssrahoo.marketplaceapi.repository.ReviewRepository;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    ReviewService reviewService;

    @Nested
    class findAll {

        private List<Review> reviewList;
        private List<ReviewResponseDto> reviewDtoList;

        @BeforeEach
        public void setUp() {
            Review review1 = new Review(UUID.randomUUID(), mock(Buyer.class), mock(Product.class), 5, "Great product!", Instant.now(), null);
            Review review2 = new Review(UUID.randomUUID(), mock(Buyer.class), mock(Product.class), 4, "Good value for money.", Instant.now(), null);
            reviewList = Arrays.asList(review1, review2);

            ReviewResponseDto dto1 = new ReviewResponseDto(review1.getReviewId(), review1.getRating(), review1.getComment());
            ReviewResponseDto dto2 = new ReviewResponseDto(review2.getReviewId(), review2.getRating(), review2.getComment());
            reviewDtoList = Arrays.asList(dto1, dto2);
        }

        @Test
        public void testFindAll_emptyList() {
            when(reviewRepository.findAll()).thenReturn(Collections.emptyList());
            List<ReviewResponseDto> result = reviewService.findAll();
            assertTrue(result.isEmpty());
        }

        @Test
        public void testFindAll_withReviews() {
            when(reviewRepository.findAll()).thenReturn(reviewList);
            List<ReviewResponseDto> result = reviewService.findAll();
            assertEquals(2, result.size());
            assertEquals(reviewDtoList.get(0).reviewId(), result.get(0).reviewId());
            assertEquals(reviewDtoList.get(0).rating(), result.get(0).rating());
            assertEquals(reviewDtoList.get(0).comment(), result.get(0).comment());
        }

        @Test
        public void testFindAll_repositoryMethodCalled() {
            when(reviewRepository.findAll()).thenReturn(reviewList);
            reviewService.findAll();
            verify(reviewRepository, times(1)).findAll();
        }

//        @Test
//        public void testFindAll_nullReturn() {
//            when(reviewRepository.findAll()).thenReturn(null);
//            List<ReviewResponseDto> result = reviewService.findAll();
//            assertNotNull(result);
//            assertTrue(result.isEmpty());
//        }
    }

    @Nested
    class findById {
        private UUID reviewId;
        private Review review;

        @BeforeEach
        public void setUp() {
            reviewId = UUID.randomUUID();
            review = new Review(reviewId, mock(Buyer.class), mock(Product.class), 5, "Great product!", Instant.now(), null);
        }

        @Test
        public void testFindById_whenReviewIsFound() {
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            Optional<ReviewResponseDto> result = reviewService.findById(reviewId);

            assertTrue(result.isPresent());
            assertEquals(reviewId, result.get().reviewId());
            assertEquals(5, result.get().rating());
            assertEquals("Great product!", result.get().comment());
            verify(reviewRepository, times(1)).findById(reviewId);
        }

        @Test
        public void testFindById_whenReviewIsNotFound() {
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            Optional<ReviewResponseDto> result = reviewService.findById(reviewId);

            assertFalse(result.isPresent());
            verify(reviewRepository, times(1)).findById(reviewId);
        }

    }


    @Nested
    class updateById{

        private UUID reviewId;
        private Review review;
        private ReviewUpdateDto reviewUpdateDto;

        @BeforeEach
        void setUp() {
            reviewId = UUID.randomUUID();
            review = new Review(reviewId, mock(Buyer.class), mock(Product.class), 4, "Good product", Instant.now(), null);
            reviewUpdateDto = new ReviewUpdateDto(5, "Excellent product!");
        }

        @Test
        void testUpdateById_ReviewExists_WithBothRatingAndComment() {
            // Arrange
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            // Act
            reviewService.updateById(reviewId, reviewUpdateDto);

            // Assert
            verify(reviewRepository).save(review);  // Ensure save is called
            assertEquals(5, review.getRating());  // Rating should be updated
            assertEquals("Excellent product!", review.getComment());  // Comment should be updated
        }

        @Test
        void testUpdateById_ReviewExists_WithOnlyRating() {
            // Arrange
            ReviewUpdateDto dto = new ReviewUpdateDto(3, null); // Only update rating
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            // Act
            reviewService.updateById(reviewId, dto);

            // Assert
            verify(reviewRepository).save(review);  // Ensure save is called
            assertEquals(3, review.getRating());  // Rating should be updated
            assertEquals("Good product", review.getComment());  // Comment should stay the same
        }

        @Test
        void testUpdateById_ReviewExists_WithOnlyComment() {
            // Arrange
            ReviewUpdateDto dto = new ReviewUpdateDto(null, "Amazing product!"); // Only update comment
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            // Act
            reviewService.updateById(reviewId, dto);

            // Assert
            verify(reviewRepository).save(review);  // Ensure save is called
            assertEquals(4, review.getRating());  // Rating should stay the same
            assertEquals("Amazing product!", review.getComment());  // Comment should be updated
        }

        @Test
        void testUpdateById_ReviewExists_WithNoChanges() {
            // Arrange
            ReviewUpdateDto dto = new ReviewUpdateDto(null, null); // No update values
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            // Act
            reviewService.updateById(reviewId, dto);

            // Assert
            verify(reviewRepository).save(review);  // Ensure save is called
        }

        @Test
        void testUpdateById_ReviewDoesNotExist() {
            // Arrange
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            // Act
            reviewService.updateById(reviewId, reviewUpdateDto);

            // Assert
            verify(reviewRepository, never()).save(any());  // Ensure save is NOT called
        }


    }

    @Nested
    class deleteById{
        @Test
        void testDeleteById_ReviewExists() {
            // Arrange
            UUID id = UUID.randomUUID();
            when(reviewRepository.existsById(id)).thenReturn(true); // Simulate that the review exists

            // Act
            reviewService.deleteById(id);

            // Assert
            verify(reviewRepository, times(1)).deleteById(id); // Verify deleteById was called exactly once
        }


        @Test
        void testDeleteById_ReviewDoesNotExist() {
            // Arrange
            UUID id = UUID.randomUUID();
            when(reviewRepository.existsById(id)).thenReturn(false); // Simulate that the review does not exist

            // Act
            reviewService.deleteById(id);

            // Assert
            verify(reviewRepository, times(0)).deleteById(id); // Verify deleteById was not called
        }

        @Test
        void testDeleteById_WhenExistsIsFalse() {
            UUID id = UUID.randomUUID();
            when(reviewRepository.existsById(id)).thenReturn(false);  // Review doesn't exist

            reviewService.deleteById(id);

            verify(reviewRepository, times(0)).deleteById(id);  // Ensure no delete operation happens
        }
    }


}