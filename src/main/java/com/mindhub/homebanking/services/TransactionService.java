package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TransactionService {

    public List<Transaction> getTransactions();

    public List<Transaction>getFilteredTransactions(LocalDateTime from_date, LocalDateTime thru_date, Long id);
    public List<TransactionDTO> getTransactionsDTO(List<Transaction> transactions);
    public void deleteTransaction(Long id);
    public void save(Transaction transaction);
}
