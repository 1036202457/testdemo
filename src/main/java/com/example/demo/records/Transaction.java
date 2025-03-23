package com.example.demo.records;

import java.math.BigDecimal;
import java.util.Date;

public record Transaction(
        String id,
        BigDecimal amount,
        String version,
        Date updatedDate,
        Date createdDate,
        String description) {
}

