package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanServiceImp implements LoanService{

    @Autowired
    LoanRepository loanRepository;
    @Override
    public Loan getLoan(Long id) {
        Optional<Loan> loan = loanRepository.findById(id);
        if(loan.isPresent()) {
            return loan.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Loan> getLoans() {
        return loanRepository.findAll();
    }

    @Override
    public LoanDTO getLoanDTO(Loan loan) {
        return new LoanDTO(loan);
    }

    @Override
    public List<LoanDTO> getLoansDTO(List<Loan> loans) {
        return loans
                .stream()
                .map(LoanDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Loan findByName(String name) {
        return loanRepository.findByName(name);
    }

    @Override
    public void save(Loan loan) {
        loanRepository.save(loan);
    }
}
