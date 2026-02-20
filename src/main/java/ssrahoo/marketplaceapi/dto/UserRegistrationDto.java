package ssrahoo.marketplaceapi.dto;

import java.math.BigDecimal;

public record UserRegistrationDto(
        String username,
        String email,
        String password,
        BigDecimal wallet
) {}