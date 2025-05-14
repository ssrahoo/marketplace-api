package ssrahoo.marketplaceapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssrahoo.marketplaceapi.dto.ProductRegistrationDto;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.entity.Seller;
import ssrahoo.marketplaceapi.entity.User;
import ssrahoo.marketplaceapi.repository.ProductRepository;
import ssrahoo.marketplaceapi.repository.SellerRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    SellerRepository sellerRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    SellerService sellerService;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    @Nested
    class saveProduct{
        @Test
        @DisplayName("METHOD saveProduct SHOULD save product WHEN required data is present.")
        void shouldSaveProductWhenRequiredDataIsPresent() {
            // arrange

            UUID sellerId = UUID.randomUUID();
            ProductRegistrationDto productRegistrationDto = new ProductRegistrationDto(
                    "productRegistrationDto name",
                    100.0,
                    1
            );

            Seller seller = new Seller(
                    new User(
                            "seller username",
                            "seller email",
                            "seller password",
                            0.0,
                            Instant.now(),
                            null
                    )
            );
            seller.setSellerId(UUID.randomUUID());

            Product product = new Product();
            product.setProductId(UUID.randomUUID());

            doReturn(Optional.of(seller)).when(sellerRepository).findById(uuidArgumentCaptor.capture());
            doReturn(product).when(productRepository).save(productArgumentCaptor.capture());

            // act
            sellerService.saveProduct(sellerId, productRegistrationDto);

            // assert
            assertEquals(sellerId, uuidArgumentCaptor.getValue());
            assertEquals(seller.getSellerId(), productArgumentCaptor.getValue().getSeller().getSellerId());

            assertEquals(productRegistrationDto.name(), productArgumentCaptor.getValue().getName());
            assertEquals(productRegistrationDto.unitPrice(), productArgumentCaptor.getValue().getUnitPrice());
            assertEquals(productRegistrationDto.stock(), productArgumentCaptor.getValue().getStock());

            verify(sellerRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(productRepository, times(1)).save(productArgumentCaptor.getValue());

        }

        //TODO: add remaining unit tests

    }

}