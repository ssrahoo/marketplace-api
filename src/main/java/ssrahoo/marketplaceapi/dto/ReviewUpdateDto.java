package ssrahoo.marketplaceapi.dto;

public record ReviewUpdateDto(
        Integer rating,
        String comment
) {}