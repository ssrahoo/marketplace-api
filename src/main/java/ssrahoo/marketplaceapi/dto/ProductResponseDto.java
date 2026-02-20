package ssrahoo.marketplaceapi.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto(
        UUID productId,
        String name,
        BigDecimal unitPrice,
        Integer stock
) {}
