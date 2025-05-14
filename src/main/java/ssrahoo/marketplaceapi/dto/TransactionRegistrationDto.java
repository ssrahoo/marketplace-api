package ssrahoo.marketplaceapi.dto;

import java.util.UUID;

public record TransactionRegistrationDto(
        String productId,
        Integer amount
) {}
