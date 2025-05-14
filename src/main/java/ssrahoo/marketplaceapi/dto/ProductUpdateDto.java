package ssrahoo.marketplaceapi.dto;

public record ProductUpdateDto(
        Double unitPrice,
        Integer stock
) {}