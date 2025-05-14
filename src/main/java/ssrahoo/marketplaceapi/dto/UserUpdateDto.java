package ssrahoo.marketplaceapi.dto;

public record UserUpdateDto(
        String username,
        String password,
        Double wallet
) {}