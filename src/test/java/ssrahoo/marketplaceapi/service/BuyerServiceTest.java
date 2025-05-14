package ssrahoo.marketplaceapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ssrahoo.marketplaceapi.dto.TransactionRegistrationDto;
import ssrahoo.marketplaceapi.entity.*;
import ssrahoo.marketplaceapi.repository.*;

import java.time.Instant;
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
                    500.0,
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
                            500.0,
                            Instant.now(),
                            null
                    )),
                    "productname",
                    100.0,
                    1,
                    Instant.now(),
                    null
            );

            double buyerWalletBefore = buyer.getUser().getWallet();
            double sellerWalletBefore = product.getSeller().getUser().getWallet();
            int productStockBefore = product.getStock();

            var buyerId = UUID.randomUUID();
            var transactionRegistrationDto = new TransactionRegistrationDto(
                    UUID.randomUUID().toString(),
                    1
            );

            double totalPrice = product.getUnitPrice() * transactionRegistrationDto.amount();

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
                    - totalPrice,
                    buyer.getUser().getWallet() - buyerWalletBefore
            );

            assertEquals(
                    totalPrice,
                    product.getSeller().getUser().getWallet() - sellerWalletBefore
            );

            assertEquals(
                    - transactionRegistrationDto.amount(),
                    product.getStock() - productStockBefore
            );

            verify(buyerProductRepository, times(1)).save(transactionArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("METHOD saveTransaction SHOULD not save transaction WHEN it is not doable.")
        void shouldNotSaveTransaction_whenItIsNotDoable() {
            //TODO: shouldNotSaveTransaction_whenItIsNotDoable
        }

        @Test
        @DisplayName("METHOD saveTransaction SHOULD throw exception WHEN exception occurs.")
        void shouldThrowException_whenExceptionOccurs(){
            //TODO shouldThrowException_whenExceptionOccurs
        }

    }

}