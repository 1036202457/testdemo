package com.example.demo.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionUpdateRequest (
        @NotBlank(message = "id must not blank") String id,
        @NotBlank(message = "version must not blank")String version,
        @NotNull(message = "Amount must not null") @Positive(message = "Amount must be positive") BigDecimal amount,
        @NotBlank(message = "Desc must not blank") String desc
) {
}
