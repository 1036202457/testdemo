package com.example.demo.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionDeleteRequest(
        @NotBlank(message = "id must not blank") String id,
        @NotBlank(message = "version must not blank")String version
) {
}
