package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;

import java.util.List;

public interface TransactionService {

    public List<Transaction> getTransactions();
    public List<TransactionDTO> getTransactionsDTO(List<Transaction> transactions);
    public void save(Transaction transaction);
}
