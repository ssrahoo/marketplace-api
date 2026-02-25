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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ssrahoo.marketplaceapi.dto.ProductRegistrationDto;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.entity.Seller;
import ssrahoo.marketplaceapi.entity.User;
import ssrahoo.marketplaceapi.repository.ProductRepository;
import ssrahoo.marketplaceapi.repository.SellerRepository;

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
        void shouldSaveProduct_whenRequiredDataIsPresent() {
            // arrange

            UUID sellerId = UUID.randomUUID();
            ProductRegistrationDto productRegistrationDto = new ProductRegistrationDto(
                    "productRegistrationDto name",
                    BigDecimal.valueOf(100.0),
                    1
            );

            Seller seller = new Seller(
                    new User(
                            "seller username",
                            "seller email",
                            "seller password",
                            BigDecimal.valueOf(0.0),
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

        @Test
        @DisplayName("METHOD saveProduct SHOULD throw exception WHEN seller not found.")
        void shouldThrowException_whenSellerNotFound() {
            // Arrange
            UUID sellerId = UUID.randomUUID();

            ProductRegistrationDto dto =
                    new ProductRegistrationDto("Product A", BigDecimal.TEN, 5);

            when(sellerRepository.findById(sellerId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> sellerService.saveProduct(sellerId, dto)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    class findAllProducts{

        @Test
        @DisplayName("METHOD saveProduct SHOULD return all products WHEN seller is found.")
        void shouldReturnAllProducts_whenSellerIsFound() {
            // Arrange
            UUID sellerId = UUID.randomUUID();

            Seller seller = new Seller();
            seller.setSellerId(sellerId);

            Product product1 = new Product();
            product1.setProductId(UUID.randomUUID());
            product1.setName("Product A");
            product1.setUnitPrice(BigDecimal.TEN);
            product1.setStock(5);

            Product product2 = new Product();
            product2.setProductId(UUID.randomUUID());
            product2.setName("Product B");
            product2.setUnitPrice(BigDecimal.ONE);
            product2.setStock(10);

            seller.setProducts(List.of(product1, product2));

            when(sellerRepository.findById(sellerId))
                    .thenReturn(Optional.of(seller));

            // Act
            List<ProductResponseDto> result =
                    sellerService.findAllProducts(sellerId);

            // Assert
            assertEquals(2, result.size());

            assertEquals(product1.getProductId(), result.get(0).productId());
            assertEquals("Product A", result.get(0).name());
            assertEquals(BigDecimal.TEN, result.get(0).unitPrice());
            assertEquals(5, result.get(0).stock());

            assertEquals(product2.getProductId(), result.get(1).productId());
            assertEquals("Product B", result.get(1).name());

            verify(sellerRepository).findById(sellerId);
        }

        @Test
        @DisplayName("METHOD saveProduct SHOULD return empty list WHEN seller has no products.")
        void shouldReturnEmptyList_whenSellerHasNoProducts() {
            // Arrange
            UUID sellerId = UUID.randomUUID();

            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            seller.setProducts(Collections.emptyList());

            when(sellerRepository.findById(sellerId))
                    .thenReturn(Optional.of(seller));

            // Act
            List<ProductResponseDto> result =
                    sellerService.findAllProducts(sellerId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(sellerRepository).findById(sellerId);
        }


        @Test
        @DisplayName("METHOD saveProduct SHOULD throw exception WHEN seller not found.")
        void shouldThrowException_whenSellerNotFound() {
            // Arrange
            UUID sellerId = UUID.randomUUID();

            when(sellerRepository.findById(sellerId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> sellerService.findAllProducts(sellerId)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

            verify(sellerRepository).findById(sellerId);
            verifyNoInteractions(productRepository);
        }
    }

}