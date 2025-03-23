package com.example.demo.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotBlank(message = "id must not blank") String id,
        @NotNull(message = "Amount must not null") @Positive(message = "Amount must be positive") BigDecimal amount,
        @NotBlank(message = "Desc must not blank") String desc
) {
}
