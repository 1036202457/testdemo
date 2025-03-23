package com.example.demo.service;

import com.example.demo.exception.ElementRepeatException;
import com.example.demo.po.TransactionModifyResult;
import com.example.demo.records.Transaction;
import com.example.demo.records.TransactionCreateRequest;
import com.example.demo.records.TransactionUpdateRequest;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    final TransactionRepository repository;

    public Transaction createTransaction(TransactionCreateRequest request) {
        Transaction newTx = repository.save(request);
        if (newTx == null) {
            throw new ElementRepeatException("Id repeated");
        }
        return newTx;
    }

    public Transaction updateTransaction(TransactionUpdateRequest request) {
        TransactionModifyResult res = repository.update(request);
        if (res.getError() != null) {
            throw res.getError();
        }
        return res.getData();
    }

    public List<Transaction> getTransactions(int page, int size) {
        List<Transaction> sorted = repository.getSorted();
        int start = page * size;
        if (start >= sorted.size()) {
            return new ArrayList<>();
        }
        int end = start + size;
        if (end > sorted.size()) {
            end = sorted.size();
        }
        return sorted.subList(start, end);
    }

    public void deleteTransaction(String id, String ver) {
        TransactionModifyResult res = repository.delete(id, ver);
        if (res.getError() != null) {
            throw res.getError();
        }
    }
}