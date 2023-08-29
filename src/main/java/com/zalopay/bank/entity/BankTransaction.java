package com.zalopay.bank.entity;


import com.zalopay.bank.enums.TransactionStatusEnum;
import com.zalopay.bank.enums.TransactionType;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bank_transaction")
@Data
public class BankTransaction {
    @Id
    private String id;
    private String numAcc;
    private Long amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;
    @Enumerated(EnumType.STRING)
    private TransactionType transType;
    private Timestamp createdTime;
}
