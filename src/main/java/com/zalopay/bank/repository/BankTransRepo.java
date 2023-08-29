package com.zalopay.bank.repository;

import com.zalopay.bank.entity.BankAccount;
import com.zalopay.bank.entity.BankTransaction;
import org.springframework.data.repository.CrudRepository;

public interface BankTransRepo extends CrudRepository<BankTransaction, String> {
}
