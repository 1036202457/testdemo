package com.example.demo.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TransactionTypeEnum {
    DEBIT(0), CREDIT(1);
    final int type;
}
