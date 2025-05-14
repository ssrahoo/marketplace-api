package ssrahoo.marketplaceapi.dto;

import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDto(
        String buyerUsername,
        UUID buyerId,
        String productName,
        UUID productId,
        Integer amount,
        Double totalPrice,
        Instant created,
        Instant modified
) {}
