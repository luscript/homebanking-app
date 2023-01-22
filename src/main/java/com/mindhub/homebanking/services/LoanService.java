package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    public Loan getLoan(Long id);
    public List<Loan> getLoans();
    public LoanDTO getLoanDTO(Loan loan);
    public List<LoanDTO> getLoansDTO(List<Loan> loans);
    public Loan findByName(String name);
    public void save(Loan loan);
}
