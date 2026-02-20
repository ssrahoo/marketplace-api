package ssrahoo.marketplaceapi.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDto(
        String buyerUsername,
        UUID buyerId,
        String productName,
        UUID productId,
        Integer amount,
        BigDecimal totalPrice,
        Instant created,
        Instant modified
) {}
