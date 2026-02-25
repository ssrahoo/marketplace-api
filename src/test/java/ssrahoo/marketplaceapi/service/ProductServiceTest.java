package ssrahoo.marketplaceapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.dto.ProductUpdateDto;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.entity.Seller;
import ssrahoo.marketplaceapi.repository.ProductRepository;

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
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Nested
    class findAll {
        private Product product1;
        private Product product2;

        @BeforeEach
        void setUp() {
            product1 = new Product(UUID.randomUUID(), mock(Seller.class), "Laptop", BigDecimal.valueOf(1000), 10, Instant.now(), null);
            product2 = new Product(UUID.randomUUID(), mock(Seller.class), "Mouse", BigDecimal.valueOf(50), 100, Instant.now(), null);
        }

        @Test
        void shouldReturnAllProductsMappedToDto() {
            // Given
            List<Product> products = List.of(product1, product2);
            when(productRepository.findAll()).thenReturn(products);

            // When
            List<ProductResponseDto> result = productService.findAll();

            // Then
            assertEquals(2, result.size());

            assertEquals(product1.getProductId(), result.get(0).productId());
            assertEquals(product1.getName(), result.get(0).name());
            assertEquals(product1.getUnitPrice(), result.get(0).unitPrice());
            assertEquals(product1.getStock(), result.get(0).stock());

            assertEquals(product2.getProductId(), result.get(1).productId());
            assertEquals(product2.getName(), result.get(1).name());
            assertEquals(product2.getUnitPrice(), result.get(1).unitPrice());
            assertEquals(product2.getStock(), result.get(1).stock());

            verify(productRepository, times(1)).findAll();
        }

        @Test
        void shouldReturnEmptyListWhenNoProductsExist() {
            // Given
            when(productRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<ProductResponseDto> result = productService.findAll();

            // Then
            assertTrue(result.isEmpty());
            verify(productRepository, times(1)).findAll();
        }

    }

    @Nested
    class findById {
        private UUID productId;
        private Product product;

        @BeforeEach
        void setUp() {
            productId = UUID.randomUUID();
            product = new Product();
            product.setProductId(productId);
            product.setName("Laptop");
            product.setUnitPrice(BigDecimal.valueOf(1200.00));
            product.setStock(10);
        }

        @Test
        void findById_shouldReturnProductResponseDto_whenProductExists() {
            // Arrange
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            // Act
            Optional<ProductResponseDto> result = productService.findById(productId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(productId, result.get().productId());
            assertEquals("Laptop", result.get().name());
            assertEquals(BigDecimal.valueOf(1200.00), result.get().unitPrice());
            assertEquals(10, result.get().stock());

            verify(productRepository, times(1)).findById(productId);
        }


        @Test
        void findById_shouldReturnEmptyOptional_whenProductDoesNotExist() {
            // Arrange
            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // Act
            Optional<ProductResponseDto> result = productService.findById(productId);

            // Assert
            assertFalse(result.isPresent());

            verify(productRepository, times(1)).findById(productId);
        }

    }


    @Nested
    class updateById{
        private UUID productId;
        private Product product;
        private ProductUpdateDto productUpdateDto;

        @BeforeEach
        public void setUp() {
            productId = UUID.randomUUID();
            product = new Product(productId, mock(Seller.class), "Product Name", BigDecimal.valueOf(100.0), 10, Instant.now(), null);
            productUpdateDto = mock(ProductUpdateDto.class);
        }


        @Test
        public void testUpdateById_ProductNotFound() {
            // Arrange
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // Act
            productService.updateById(productId, productUpdateDto);

            // Assert
            verify(productRepository, never()).save(any());
        }

        @Test
        public void testUpdateById_ProductFoundNoUpdate() {
            // Arrange
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productUpdateDto.unitPrice()).thenReturn(null);
            when(productUpdateDto.stock()).thenReturn(null);

            // Act
            productService.updateById(productId, productUpdateDto);

            // Assert
            assertEquals(product.getUnitPrice().doubleValue(), 100.0);
            assertEquals(product.getStock(), 10);
            assertNotNull(product.getModified());
            verify(productRepository).save(product);
        }

        @Test
        public void testUpdateById_ProductFoundWithUnitPrice() {
            // Arrange
            double newUnitPrice = 120.0;
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productUpdateDto.unitPrice()).thenReturn(BigDecimal.valueOf(newUnitPrice));
            when(productUpdateDto.stock()).thenReturn(null);

            // Act
            productService.updateById(productId, productUpdateDto);

            // Assert
            assertEquals(product.getUnitPrice().doubleValue(), newUnitPrice);
            assertEquals(product.getStock(), 10);
            assertNotNull(product.getModified());
            verify(productRepository).save(product);
        }

        @Test
        public void testUpdateById_ProductFoundWithStock() {
            // Arrange
            int newStock = 20;
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productUpdateDto.unitPrice()).thenReturn(null);
            when(productUpdateDto.stock()).thenReturn(newStock);

            // Act
            productService.updateById(productId, productUpdateDto);

            // Assert
            assertEquals(product.getUnitPrice().doubleValue(), 100.0);
            assertEquals(product.getStock(), newStock);
            assertNotNull(product.getModified());
            verify(productRepository).save(product);
        }

        @Test
        public void testUpdateById_ProductFoundWithBothUpdates() {
            // Arrange
            double newUnitPrice = 150.0;
            int newStock = 30;
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productUpdateDto.unitPrice()).thenReturn(BigDecimal.valueOf(newUnitPrice));
            when(productUpdateDto.stock()).thenReturn(newStock);

            // Act
            productService.updateById(productId, productUpdateDto);

            // Assert
            assertEquals(product.getUnitPrice().doubleValue(), newUnitPrice);
            assertEquals(product.getStock(), newStock);
            assertNotNull(product.getModified());
            verify(productRepository).save(product);
        }

    }

    @Nested
    class deleteById{
        private UUID productId;

        @BeforeEach
        void setUp() {
            // Initialize the productId with a sample UUID
            productId = UUID.randomUUID();
        }

        @Test
        void deleteById_ProductExists_DeletesProduct() {
            // Arrange: Mock existsById to return true
            when(productRepository.existsById(productId)).thenReturn(true);

            // Act: Call the method to test
            productService.deleteById(productId);

            // Assert: Verify that deleteById was called
            verify(productRepository, times(1)).deleteById(productId);
        }

        @Test
        void deleteById_ProductDoesNotExist_DoesNotDeleteProduct() {
            // Arrange: Mock existsById to return false
            when(productRepository.existsById(productId)).thenReturn(false);

            // Act: Call the method to test
            productService.deleteById(productId);

            // Assert: Verify that deleteById was not called
            verify(productRepository, times(0)).deleteById(productId);
        }

        @Test
        void deleteById_MultipleCalls_VerifiesDeleteBehavior() {
            // Arrange: Mock existsById to return true for the first call, false for the second
            when(productRepository.existsById(productId)).thenReturn(true, false);

            // Act: Call the method twice
            productService.deleteById(productId); // should delete
            productService.deleteById(productId); // should not delete

            // Assert: Verify deleteById is called only once
            verify(productRepository, times(1)).deleteById(productId);
        }

    }


}