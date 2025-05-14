package ssrahoo.marketplaceapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssrahoo.marketplaceapi.dto.ProductRegistrationDto;
import ssrahoo.marketplaceapi.dto.ProductResponseDto;
import ssrahoo.marketplaceapi.service.SellerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/{sellerId}/product")
    public ResponseEntity<Void> saveProduct(
            @PathVariable("sellerId") UUID sellerId,
            @RequestBody ProductRegistrationDto productRegistrationDto
    ){
        sellerService.saveProduct(sellerId, productRegistrationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sellerId}/product")
    public ResponseEntity<List<ProductResponseDto>> findAllProducts(
            @PathVariable("sellerId") UUID sellerId
    ){
        var products = sellerService.findAllProducts(sellerId);
        return ResponseEntity.ok(products);
    }


}
