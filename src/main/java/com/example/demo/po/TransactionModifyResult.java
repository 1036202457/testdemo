package com.example.demo.po;

import com.example.demo.records.Transaction;
import lombok.Data;


@Data
public class TransactionModifyResult {
    RuntimeException error;
    Transaction data;
}
