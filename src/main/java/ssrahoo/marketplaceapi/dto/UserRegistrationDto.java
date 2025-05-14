package ssrahoo.marketplaceapi.dto;

public record UserRegistrationDto(
        String username,
        String email,
        String password,
        Double wallet
) {}