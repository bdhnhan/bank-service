package com.zalopay.bank.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "bank_account")
@Data
public class BankAccount {
    @Id
    private String id;
    private String bankName;
    private String bankCode;
    private String fullName;
    private String numberAccount;
    private Long balance;
    private Timestamp createdTime;
    private Timestamp updatedTime;
}
