package com.zalopay.bank.repository;

import com.zalopay.bank.entity.BankAccount;
import com.zalopay.bank.entity.BankTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BankTransRepo extends CrudRepository<BankTransaction, String> {
    Optional<BankTransaction> findFirstByIdOrKeySource(String id, String keySource);
}
