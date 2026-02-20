package ssrahoo.marketplaceapi.dto;

import java.math.BigDecimal;

public record ProductRegistrationDto(
        String name,
        BigDecimal unitPrice,
        Integer stock
) {}


