package ssrahoo.marketplaceapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.dto.ProductUpdateDto;
import ssrahoo.marketplaceapi.entity.Product;
import ssrahoo.marketplaceapi.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // POST is handled by BuyerController

    // read all
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAll() {
        var products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    // read by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable("id") UUID id) {
        var product = productService.findById(id);
        return product.isPresent() ? ResponseEntity.ok(product.get()) : ResponseEntity.notFound().build();
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(
            @PathVariable("id") UUID id,
            @RequestBody ProductUpdateDto productUpdateDto
    ){
        productService.updateById(id, productUpdateDto);
        return ResponseEntity.noContent().build();
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id){
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
