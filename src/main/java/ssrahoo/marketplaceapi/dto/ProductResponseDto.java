package ssrahoo.marketplaceapi.dto;

import java.util.UUID;

public record ProductResponseDto(
        UUID productId,
        String name,
        Double unitPrice,
        Integer stock
) {}
