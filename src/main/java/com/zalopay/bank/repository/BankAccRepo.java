package com.zalopay.bank.repository;

import com.zalopay.bank.entity.BankAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BankAccRepo extends CrudRepository<BankAccount, String> {
    Optional<BankAccount> findByNumberAccount(String numberAcc);
}
