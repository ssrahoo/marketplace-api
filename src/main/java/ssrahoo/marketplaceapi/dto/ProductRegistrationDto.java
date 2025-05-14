package ssrahoo.marketplaceapi.dto;

public record ProductRegistrationDto(
        String name,
        Double unitPrice,
        Integer stock
) {}


