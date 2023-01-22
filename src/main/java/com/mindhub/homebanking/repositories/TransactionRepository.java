package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RepositoryRestResource
public interface TransactionRepository extends JpaRepository <Transaction, Long> {

    @Query(value="SELECT * FROM TRANSACTION WHERE account_id = :id AND date BETWEEN :from_date AND :thru_date", nativeQuery=true)
    List<Transaction> getFilteredTransactions(@Param("from_date") LocalDateTime from_date, @Param("thru_date") LocalDateTime thru_date, @Param("id")Long id);
}
