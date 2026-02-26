package ssrahoo.marketplaceapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssrahoo.marketplaceapi.dto.ProductRegistrationDto;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.repository.ProductRepository;
import ssrahoo.marketplaceapi.repository.SellerRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SellerService {
    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);

    private SellerRepository sellerRepository;
    private ProductRepository productRepository;

    public SellerService(SellerRepository sellerRepository, ProductRepository productRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
    }

    public void saveProduct(UUID sellerId, ProductRegistrationDto productRegistrationDto){
        logger.debug("Registering product");

        var seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Product product = new Product(
                null,
                seller,
                productRegistrationDto.name(),
                productRegistrationDto.unitPrice(),
                productRegistrationDto.stock(),
                Instant.now(),
                null
        );
        var id = productRepository.save(product).getProductId();

        logger.info("Product registered productId={}", id);
    }


    public List<ProductResponseDto> findAllProducts(UUID sellerId){
        var seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return seller.getProducts()
                .stream()
                .map(p ->
                        new ProductResponseDto(
                                p.getProductId(),
                                p.getName(),
                                p.getUnitPrice(),
                                p.getStock()
                        )
                )
                .toList();
    }

}
