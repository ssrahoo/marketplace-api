package ssrahoo.marketplaceapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ssrahoo.marketplaceapi.dto.ReviewRegistrationDto;
import ssrahoo.marketplaceapi.dto.ReviewResponseDto;
import ssrahoo.marketplaceapi.dto.TransactionRegistrationDto;
import ssrahoo.marketplaceapi.dto.TransactionResponseDto;
import ssrahoo.marketplaceapi.entity.*;
import ssrahoo.marketplaceapi.repository.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyerServiceTest {

    @Mock
    BuyerRepository buyerRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    BuyerProductRepository buyerProductRepository;

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    BuyerService buyerService;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    @Captor
    ArgumentCaptor<Transaction> transactionArgumentCaptor;

    /* saveTransaction - legacy

    @Nested
    class saveTransaction{
        @Test
        @DisplayName("METHOD saveTransaction SHOULD save transaction WHEN it is doable and required data is present.")
        void shouldSaveTransaction_whenItIsDoableAndRequiredDataIsPresent() {
            // arrange

            Buyer buyer = new Buyer(new User(
                    "buyername",
                    "buyeremail",
                    "buyerpassword",
                    BigDecimal.valueOf(500.0),
                    Instant.now(),
                    null
            ));

            buyer.setBuyerId(UUID.randomUUID());

            Product product = new Product(
                    UUID.randomUUID(),
                    new Seller(new User(
                            "sellername",
                            "selleremail",
                            "sellerpassword",
                            BigDecimal.valueOf(0.0),
                            Instant.now(),
                            null
                    )),
                    "productname",
                    BigDecimal.valueOf(500.0),
                    1,
                    Instant.now(),
                    null
            );

            BigDecimal buyerWalletBefore = buyer.getUser().getWallet();
            BigDecimal sellerWalletBefore = product.getSeller().getUser().getWallet();
            int productStockBefore = product.getStock();

            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    1
            );

            BigDecimal totalPrice = product.getUnitPrice().multiply(BigDecimal.valueOf(transactionRegistrationDto.amount()));

            doReturn(Optional.of(buyer)).when(buyerRepository).findById(uuidArgumentCaptor.capture());
            doReturn(Optional.of(product)).when(productRepository).findById(uuidArgumentCaptor.capture());
            doReturn(null).when(buyerProductRepository).save(transactionArgumentCaptor.capture());

            // act
            var ok = buyerService.saveTransaction(buyerId, transactionRegistrationDto);

            // assert
            assertTrue(ok);

            assertEquals(buyerId, uuidArgumentCaptor.getAllValues().get(0));
            assertEquals(UUID.fromString(transactionRegistrationDto.productId()), uuidArgumentCaptor.getAllValues().get(1));

            assertEquals(buyer.getBuyerId(), transactionArgumentCaptor.getValue().getId().getBuyerId());
            assertEquals(product.getProductId(), transactionArgumentCaptor.getValue().getId().getProductId());
            assertEquals(buyer.getBuyerId(), transactionArgumentCaptor.getValue().getBuyer().getBuyerId());
            assertEquals(product.getProductId(), transactionArgumentCaptor.getValue().getProduct().getProductId());
            assertEquals(transactionRegistrationDto.amount(), transactionArgumentCaptor.getValue().getAmount());
            assertEquals(totalPrice, transactionArgumentCaptor.getValue().getTotalPrice());

            assertEquals(
                    totalPrice.negate(),
                    buyer.getUser().getWallet().subtract(buyerWalletBefore)
            );

            assertEquals(
                    totalPrice,
                    product.getSeller().getUser().getWallet().subtract(sellerWalletBefore)
            );

            assertEquals(
                    - transactionRegistrationDto.amount(),
                    product.getStock() - productStockBefore
            );

            verify(buyerProductRepository, times(1)).save(transactionArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("METHOD saveTransaction SHOULD not save transaction WHEN wallet is less than total price.")
        void shouldNotSaveTransaction_whenWalletIsLessThanTotalPrice() {
            Buyer buyer = new Buyer(new User(
                    "buyername",
                    "buyeremail",
                    "buyerpassword",
                    BigDecimal.valueOf(500.0),
                    Instant.now(),
                    null
            ));

            buyer.setBuyerId(UUID.randomUUID());

            Product product = new Product(
                    UUID.randomUUID(),
                    new Seller(new User(
                            "sellername",
                            "selleremail",
                            "sellerpassword",
                            BigDecimal.valueOf(0.0),
                            Instant.now(),
                            null
                    )),
                    "productname",
                    BigDecimal.valueOf(600.0),
                    1,
                    Instant.now(),
                    null
            );

            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    1
            );

            doReturn(Optional.of(buyer)).when(buyerRepository).findById(uuidArgumentCaptor.capture());
            doReturn(Optional.of(product)).when(productRepository).findById(uuidArgumentCaptor.capture());

            // act
            var ok = buyerService.saveTransaction(buyerId, transactionRegistrationDto);

            // assert
            assertFalse(ok);

            assertEquals(buyerId, uuidArgumentCaptor.getAllValues().get(0));
            assertEquals(UUID.fromString(transactionRegistrationDto.productId()), uuidArgumentCaptor.getAllValues().get(1));
        }

        @Test
        @DisplayName("METHOD saveTransaction SHOULD not save transaction WHEN product stock is less than transaction amount.")
        void shouldNotSaveTransaction_whenProductStockIsLessThanTransactionAmount() {
            Buyer buyer = new Buyer(new User(
                    "buyername",
                    "buyeremail",
                    "buyerpassword",
                    BigDecimal.valueOf(1000.0),
                    Instant.now(),
                    null
            ));

            buyer.setBuyerId(UUID.randomUUID());

            Product product = new Product(
                    UUID.randomUUID(),
                    new Seller(new User(
                            "sellername",
                            "selleremail",
                            "sellerpassword",
                            BigDecimal.valueOf(0.0),
                            Instant.now(),
                            null
                    )),
                    "productname",
                    BigDecimal.valueOf(500.0),
                    1,
                    Instant.now(),
                    null
            );

            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    2
            );

            doReturn(Optional.of(buyer)).when(buyerRepository).findById(uuidArgumentCaptor.capture());
            doReturn(Optional.of(product)).when(productRepository).findById(uuidArgumentCaptor.capture());

            // act
            var ok = buyerService.saveTransaction(buyerId, transactionRegistrationDto);

            // assert
            assertFalse(ok);

            assertEquals(buyerId, uuidArgumentCaptor.getAllValues().get(0));
            assertEquals(UUID.fromString(transactionRegistrationDto.productId()), uuidArgumentCaptor.getAllValues().get(1));
        }


        @Test
        @DisplayName("METHOD saveTransaction SHOULD not save transaction WHEN transaction amount is less than one.")
        void shouldNotSaveTransaction_whenTransactionAmountIsLessThanOne() {
            Buyer buyer = new Buyer(new User(
                    "buyername",
                    "buyeremail",
                    "buyerpassword",
                    BigDecimal.valueOf(500.0),
                    Instant.now(),
                    null
            ));

            buyer.setBuyerId(UUID.randomUUID());

            Product product = new Product(
                    UUID.randomUUID(),
                    new Seller(new User(
                            "sellername",
                            "selleremail",
                            "sellerpassword",
                            BigDecimal.valueOf(0.0),
                            Instant.now(),
                            null
                    )),
                    "productname",
                    BigDecimal.valueOf(500.0),
                    1,
                    Instant.now(),
                    null
            );

            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    0
            );

            doReturn(Optional.of(buyer)).when(buyerRepository).findById(uuidArgumentCaptor.capture());
            doReturn(Optional.of(product)).when(productRepository).findById(uuidArgumentCaptor.capture());

            // act
            var ok = buyerService.saveTransaction(buyerId, transactionRegistrationDto);

            // assert
            assertFalse(ok);

            assertEquals(buyerId, uuidArgumentCaptor.getAllValues().get(0));
            assertEquals(UUID.fromString(transactionRegistrationDto.productId()), uuidArgumentCaptor.getAllValues().get(1));
        }



        @Test
        @DisplayName("METHOD saveTransaction SHOULD throw exception WHEN buyer id is not found.")
        void shouldThrowException_whenBuyerIdIsNotFound(){
            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    0
            );

            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(buyerRepository).findById(uuidArgumentCaptor.capture());

            // act & assert
            assertThrows(ResponseStatusException.class, () -> buyerService.saveTransaction(buyerId, transactionRegistrationDto));
        }

        @Test
        @DisplayName("METHOD saveTransaction SHOULD throw exception WHEN product id is not found.")
        void shouldThrowException_whenProductIdIsNotFound(){
            Buyer buyer = new Buyer(new User(
                    "buyername",
                    "buyeremail",
                    "buyerpassword",
                    BigDecimal.valueOf(500.0),
                    Instant.now(),
                    null
            ));

            buyer.setBuyerId(UUID.randomUUID());

            Product product = new Product(
                    UUID.randomUUID(),
                    new Seller(new User(
                            "sellername",
                            "selleremail",
                            "sellerpassword",
                            BigDecimal.valueOf(0.0),
                            Instant.now(),
                            null
                    )),
                    "productname",
                    BigDecimal.valueOf(500.0),
                    1,
                    Instant.now(),
                    null
            );

            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    2
            );

            doReturn(Optional.of(buyer)).when(buyerRepository).findById(uuidArgumentCaptor.capture());
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(productRepository).findById(uuidArgumentCaptor.capture());

            // act & assert
            assertThrows(ResponseStatusException.class, () -> buyerService.saveTransaction(buyerId, transactionRegistrationDto));
        }

    }

    */

    // saveTransaction - cleaner version
    @Nested
    class saveTransaction{
        private UUID buyerId;
        private UUID productId;
        private Buyer buyer;
        private Product product;
        private User buyerUser;
        private User sellerUser;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            buyerId = UUID.randomUUID();
            productId = UUID.randomUUID();
            initUsers();
            initBuyer();
            initProduct();
        }

        private void initUsers() {
            buyerUser = new User();
            buyerUser.setWallet(BigDecimal.valueOf(1000));

            sellerUser = new User();
            sellerUser.setWallet(BigDecimal.valueOf(500));
        }

        private void initBuyer() {
            buyer = new Buyer();
            buyer.setBuyerId(buyerId);
            buyer.setUser(buyerUser);
        }

        private void initProduct() {
            product = new Product();
            product.setProductId(productId);
            product.setUnitPrice(BigDecimal.valueOf(50));
            product.setStock(10);
            product.setSeller(new Seller(sellerUser));
        }

        private TransactionRegistrationDto createDto(int amount) {
            return new TransactionRegistrationDto(productId.toString(), amount);
        }

        private void mockRepositories(Optional<Buyer> buyerOpt, Optional<Product> productOpt) {
            when(buyerRepository.findById(buyerId)).thenReturn(buyerOpt);
            when(productRepository.findById(productId)).thenReturn(productOpt);
        }

        @Test
        void testBuyerNotFound() {
            when(buyerRepository.findById(buyerId)).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class,
                    () -> buyerService.saveTransaction(buyerId, createDto(2)));
        }

        @Test
        void testProductNotFound() {
            mockRepositories(Optional.of(buyer), Optional.empty());

            assertThrows(ResponseStatusException.class,
                    () -> buyerService.saveTransaction(buyerId, createDto(2)));
        }

        @Test
        void testInsufficientFunds() {
            buyerUser.setWallet(BigDecimal.valueOf(50)); // less than totalPrice
            mockRepositories(Optional.of(buyer), Optional.of(product));

            boolean result = buyerService.saveTransaction(buyerId, createDto(2));
            assertFalse(result);
        }

        @Test
        void testInsufficientStock() {
            mockRepositories(Optional.of(buyer), Optional.of(product));
            // amount > stock
            assertFalse(buyerService.saveTransaction(buyerId, createDto(20)));
        }

        @Test
        void testInvalidAmount() {
            mockRepositories(Optional.of(buyer), Optional.of(product));
            // amount < 1
            assertFalse(buyerService.saveTransaction(buyerId, createDto(0)));
        }

        @Test
        void testSuccessfulTransaction() {
            mockRepositories(Optional.of(buyer), Optional.of(product));

            boolean result = buyerService.saveTransaction(buyerId, createDto(5));
            assertTrue(result);

            // Verify wallet updates
            assertEquals(BigDecimal.valueOf(750), buyerUser.getWallet());
            assertEquals(BigDecimal.valueOf(750), sellerUser.getWallet());

            // Verify stock update
            assertEquals(5, product.getStock());

            // Verify repositories are called
            verify(userRepository).save(buyerUser);
            verify(userRepository).save(sellerUser);
            verify(productRepository).save(product);
            verify(buyerProductRepository).save(any(Transaction.class));
        }
    }

    @Nested
    class findAllTransactions{
        @Test
        @DisplayName("METHOD findAllTransactions SHOULD throw exception WHEN buyerId is not found.")
        void shouldThrowException_whenBuyerIdIsNotFound(){
            var buyerId = UUID.randomUUID();
            when(buyerRepository.findById(buyerId)).thenReturn(Optional.empty());
            assertThrows(ResponseStatusException.class, () -> buyerService.findAllTransactions(buyerId));
        }

        @Test
        @DisplayName("METHOD findAllTransactions SHOULD return empty list WHEN buyer has no transactions")
        void shouldReturnEmptyList_whenBuyerHasNoTransactions() {
            UUID buyerId = UUID.randomUUID();

            Buyer buyer = new Buyer();
            buyer.setBuyerId(buyerId);
            buyer.setBuyerProductList(Collections.emptyList());

            when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));

            List<TransactionResponseDto> result = buyerService.findAllTransactions(buyerId);

            assertTrue(result.isEmpty());
            verify(buyerRepository).findById(buyerId);
        }

        @Test
        @DisplayName("METHOD findAllTransactions SHOULD return mapped DTOs WHEN buyer exists")
        void shouldReturnTransactions_whenBuyerExists() {
            // Arrange
            UUID buyerId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            User user = new User();
            user.setUsername("john_doe");

            Buyer buyer = new Buyer();
            buyer.setBuyerId(buyerId);
            buyer.setUser(user);

            Product product = new Product();
            product.setProductId(productId);
            product.setName("Laptop");

            Transaction buyerProduct = new Transaction();
            buyerProduct.setBuyer(buyer);
            buyerProduct.setProduct(product);
            buyerProduct.setAmount(2);
            buyerProduct.setTotalPrice(BigDecimal.valueOf(50));
            buyerProduct.setCreated(Instant.now());
            buyerProduct.setModified(Instant.now());

            buyer.setBuyerProductList(List.of(buyerProduct));

            when(buyerRepository.findById(buyerId))
                    .thenReturn(Optional.of(buyer));

            // Act
            List<TransactionResponseDto> result =
                    buyerService.findAllTransactions(buyerId);

            // Assert
            assertEquals(1, result.size());

            TransactionResponseDto dto = result.get(0);

            assertEquals("john_doe", dto.buyerUsername());
            assertEquals(buyerId, dto.buyerId());
            assertEquals("Laptop", dto.productName());
            assertEquals(productId, dto.productId());
            assertEquals(2, dto.amount());
            assertEquals(BigDecimal.valueOf(50), dto.totalPrice());

            verify(buyerRepository).findById(buyerId);
        }

    }

    @Nested
    class saveReview{
        private UUID buyerId;
        private UUID productId;
        private ReviewRegistrationDto reviewRegistrationDto;

        @BeforeEach
        void setUp() {
            buyerId = UUID.randomUUID();
            productId = UUID.randomUUID();
            reviewRegistrationDto = new ReviewRegistrationDto(5, "Great product!");
        }

        @Test
        @DisplayName("METHOD saveReview SHOULD save review successfully.")
        void saveReview_shouldSaveReviewSuccessfully() {
            // arrange
            Buyer buyer = new Buyer();
            buyer.setBuyerId(buyerId);

            Product product = new Product();
            product.setProductId(productId);

            Review savedReview = new Review(UUID.randomUUID(), buyer, product, 5, "Great product!", Instant.now(), null);

            when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

            // act
            buyerService.saveReview(buyerId, productId, reviewRegistrationDto);

            // assert
            verify(buyerRepository).findById(buyerId);
            verify(productRepository).findById(productId);
            verify(reviewRepository).save(any(Review.class));
        }


        @Test
        @DisplayName("METHOD saveReview SHOULD throw WHEN buyer not found.")
        void shouldThrow_whenBuyerNotFound(){
            // arrange
            when(buyerRepository.findById(buyerId)).thenReturn(Optional.empty());

            // act & assert
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> buyerService.saveReview(buyerId, productId, reviewRegistrationDto)
            );
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            verify(buyerRepository).findById(buyerId);
            verifyNoInteractions(productRepository, reviewRepository);
        }

        @Test
        @DisplayName("METHOD saveReview SHOULD throw WHEN product not found.")
        void shouldThrow_whenProductNotFound() {
            // arrange
            Buyer buyer = new Buyer();
            buyer.setBuyerId(buyerId);

            when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // act & assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> buyerService.saveReview(buyerId, productId, reviewRegistrationDto));

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            verify(buyerRepository).findById(buyerId);
            verify(productRepository).findById(productId);
            verifyNoInteractions(reviewRepository);
        }

    }

    @Nested
    class findAllReviews{
        private UUID buyerId;
        private UUID productId;

        @BeforeEach
        void setUp() {
            buyerId = UUID.randomUUID();
            productId = UUID.randomUUID();
        }

        @Test
        @DisplayName("METHOD findAllTransactions SHOULD throw WHEN buyer not found.")
        void shouldThrow_whenBuyerNotFound() {
            // arrange
            when(buyerRepository.findById(buyerId)).thenReturn(Optional.empty());

            // act & assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> buyerService.findAllReviews(buyerId, productId));

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }

        @Test
        @DisplayName("METHOD findAllTransactions SHOULD throw WHEN product not found.")
        void shouldThrow_whenProductNotFound() {
            // arrange
            Buyer buyer = mock(Buyer.class);
            when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // act & assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> buyerService.findAllReviews(buyerId, productId));

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }

        @Test
        @DisplayName("METHOD findAllTransactions SHOULD return reviews WHEN buyer and product are found.")
        void shouldReturnReviews_whenBuyerAndProductAreFound() {
            UUID buyerId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            // Mock buyer and their reviews
            Review review1 = new Review(UUID.randomUUID(), mock(Buyer.class), mock(Product.class), 5, "Excellent", Instant.now(), null);
            Review review2 = new Review(UUID.randomUUID(), mock(Buyer.class), mock(Product.class), 3, "Average", Instant.now(), null);

            Buyer buyer = mock(Buyer.class);
            when(buyer.getReviews()).thenReturn(List.of(review1, review2));

            when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));

            // Mock product and its reviews
            Review review3 = new Review(review1.getReviewId(), mock(Buyer.class), mock(Product.class), 5, "Excellent", Instant.now(), null);
            Review review4 = new Review(UUID.randomUUID(), mock(Buyer.class), mock(Product.class), 4, "Good", Instant.now(), null);

            Product product = mock(Product.class);
            when(product.getReviews()).thenReturn(List.of(review3, review4));

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            List<ReviewResponseDto> result = buyerService.findAllReviews(buyerId, productId);

            // Only review3 should be returned because buyer only wrote review1
            assertEquals(1, result.size());
            assertEquals(review1.getReviewId(), result.get(0).reviewId());
            assertEquals(5, result.get(0).rating());
            assertEquals("Excellent", result.get(0).comment());
        }

    }


}