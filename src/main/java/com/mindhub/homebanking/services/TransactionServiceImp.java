package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImp implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getFilteredTransactions(LocalDateTime from_date, LocalDateTime thru_date, Long id) {
        return transactionRepository.getFilteredTransactions(from_date, thru_date, id);
    }

    @Override
    public List<TransactionDTO> getTransactionsDTO(List<Transaction> transactions) {
        return transactions
                .stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id).get();
        transactionRepository.delete(transaction);
    }

    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
