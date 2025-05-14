package ssrahoo.marketplaceapi.dto;

import java.util.UUID;

public record ReviewResponseDto(
        UUID reviewId,
        Integer rating,
        String comment
) {}
