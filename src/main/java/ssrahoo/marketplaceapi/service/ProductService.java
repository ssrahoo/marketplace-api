package ssrahoo.marketplaceapi.service;

import org.springframework.stereotype.Service;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.dto.ProductUpdateDto;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.repository.ProductRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // read all
    public List<ProductResponseDto> findAll(){
        return productRepository.findAll()
                .stream()
                .map(p ->
                        new ProductResponseDto(
                                p.getProductId(),
                                p.getName(),
                                p.getUnitPrice(),
                                p.getStock()
                        ))
                .toList();
    }

    // read by id
    public Optional<ProductResponseDto> findById(UUID id){
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()){
            return Optional.of(new ProductResponseDto(
                    product.get().getProductId(),
                    product.get().getName(),
                    product.get().getUnitPrice(),
                    product.get().getStock()
            ));
        }else{
            return Optional.empty();
        }

    }

    // update
    public void updateById(UUID id, ProductUpdateDto productUpdateDto){
        var result = productRepository.findById(id);

        if (result.isPresent()) {
            var product = result.get();

            if(productUpdateDto.unitPrice() != null){
                product.setUnitPrice(productUpdateDto.unitPrice());
            }

            if(productUpdateDto.stock() != null){
                product.setStock(productUpdateDto.stock());
            }

            product.setModified(Instant.now());
            productRepository.save(product);
        }
    }

    // delete
    public void deleteById(UUID id){
        if(productRepository.existsById(id)){
            productRepository.deleteById(id);
        }
    }

}
