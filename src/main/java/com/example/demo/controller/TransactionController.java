package com.example.demo.controller;


import com.example.demo.records.Transaction;
import com.example.demo.records.TransactionCreateRequest;
import com.example.demo.records.TransactionDeleteRequest;
import com.example.demo.records.TransactionUpdateRequest;
import com.example.demo.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    final TransactionService transactionService;

    @PostMapping
    public Transaction create(
            @Valid @RequestBody TransactionCreateRequest request) {
        return transactionService.createTransaction(request);
    }

    @PutMapping
    public Transaction update(
            @Valid @RequestBody TransactionUpdateRequest request) {
        return transactionService.updateTransaction(request);
    }

    @GetMapping
    public List<Transaction> list(
            @PositiveOrZero(message = "Page must be zero or positive") @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size must be positive") @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getTransactions(page, size);
    }

    @DeleteMapping
    public void delete(@RequestBody TransactionDeleteRequest req) {
        transactionService.deleteTransaction(req.id(), req.version());
    }
}