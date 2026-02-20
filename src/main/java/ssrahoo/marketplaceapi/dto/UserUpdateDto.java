package ssrahoo.marketplaceapi.dto;

import java.math.BigDecimal;

public record UserUpdateDto(
        String username,
        String password,
        BigDecimal wallet
) {}