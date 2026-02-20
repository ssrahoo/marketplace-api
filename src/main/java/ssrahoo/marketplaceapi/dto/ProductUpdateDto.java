package ssrahoo.marketplaceapi.dto;

import java.math.BigDecimal;

public record ProductUpdateDto(
        BigDecimal unitPrice,
        Integer stock
) {}