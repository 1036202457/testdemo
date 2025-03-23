package com.example.demo.repository;

import com.example.demo.exception.ElementExpiredException;
import com.example.demo.records.Transaction;
import com.example.demo.records.TransactionCreateRequest;
import com.example.demo.po.TransactionModifyResult;
import com.example.demo.records.TransactionUpdateRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@CacheConfig(cacheNames = "transactions")
public class TransactionRepository {
    private final ConcurrentHashMap<String, Transaction> store = new ConcurrentHashMap<>();

    public Transaction findById(String id) {
        return store.get(id);
    }

    @Cacheable
    public List<Transaction> getSorted() {
        return store.values().stream()
                .sorted(Comparator.comparing(Transaction::updatedDate).reversed())
                .toList();
    }

    @CacheEvict(allEntries = true, condition = "#result==null")
    public Transaction save(TransactionCreateRequest request) {
        Transaction newTx = new Transaction(
                request.id(),
                request.amount(),
                newVersionString(),
                new Date(),
                new Date(),
                request.desc()
        );
        Transaction oldTx = store.putIfAbsent(request.id(), newTx);
        return (oldTx == null) ? newTx : null;
    }

    @CacheEvict(allEntries = true, condition = "#result!=null")
    public TransactionModifyResult update(TransactionUpdateRequest req) {
        TransactionModifyResult res = new TransactionModifyResult();
        store.computeIfPresent(req.id(), (id, oldTx) -> {
            if (oldTx.version().contentEquals(req.version())) {
                res.setData(new Transaction(id, req.amount(), newVersionString(), new Date(), oldTx.createdDate(), req.desc()));
                return res.getData();
            }
            res.setError(new ElementExpiredException("Version must be latest"));
            return oldTx;
        });
        if (res.getData() == null && res.getError() == null) {
            res.setError(new NoSuchElementException("Element must exists"));
        }
        return res;
    }

    @CacheEvict(allEntries = true, condition = "#result!=null")
    public TransactionModifyResult delete(String id, String ver) {
        TransactionModifyResult res = new TransactionModifyResult();
        store.computeIfPresent(id, (tid, oldTx) -> {
            if (oldTx.version().contentEquals(ver)) {
                res.setData(oldTx);
                return null;
            }
            res.setError(new ElementExpiredException("Version must be latest"));
            return oldTx;
        });
        if (res.getData() == null && res.getError() == null) {
            res.setError(new NoSuchElementException("Element must exists"));
        }
        return res;
    }


    protected String newVersionString() {
        return String.valueOf(new Date().getTime());
    }

}
