package com.mindhub.homebanking.controller;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Autowired
    ClientService clientService;
    @Autowired
    LoanService loanService;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountService accountService;

    @RequestMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoansDTO(loanService.getLoans());
    }

    @Transactional
    @PostMapping(path = "/clients/current/loans")
    public ResponseEntity<Object> addLoan(@RequestBody LoanApplicationDTO clientLoan, Authentication auth) {

        if(clientLoan.getAmount() <= 0 || clientLoan.getPayments() == 0 || clientLoan.getDestinyAccountNumber() == null) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if(loanService.getLoan(clientLoan.getId()) == null) {
            return new ResponseEntity<>("Loan doesn't exist", HttpStatus.FORBIDDEN);
        }
        Loan loan = loanService.getLoan(clientLoan.getId());
        if(clientLoan.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("Loan max amount exceeded", HttpStatus.FORBIDDEN);
        }
        if(!loan.getPayments().contains(clientLoan.getPayments())) {
            return new ResponseEntity<>("Payments number not valid", HttpStatus.FORBIDDEN);
        }
        Account account = accountService.findByNumber(clientLoan.getDestinyAccountNumber());
        if(account == null) {
            return new ResponseEntity<>("Destiny account not found", HttpStatus.FORBIDDEN);
        }
        Client client = clientService.findByEmail(auth.getName());
        if(!client.getAccounts().contains(account)) {
            return new ResponseEntity<>("Account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        ClientLoan newClientLoan = new ClientLoan(clientLoan.getAmount() + clientLoan.getAmount()*0.2, clientLoan.getPayments(), LocalDateTime.now());
        loan.addClientLoan(newClientLoan);
        loanService.save(loan);
        clientLoanRepository.save(newClientLoan);
        Transaction transaction = new Transaction(TransactionType.CREDIT, clientLoan.getAmount(), loan.getName(), LocalDateTime.now());
        transactionRepository.save(transaction);
        client.addClientLoan(newClientLoan);
        account.addTransaction(transaction);
        account.setBalance(account.getBalance() + clientLoan.getAmount());
        accountService.save(account);
        clientService.save(client);
        return new ResponseEntity<>("Created", HttpStatus.CREATED);

    }
}
