package com.mindhub.homebanking.controller;

import com.mindhub.homebanking.dtos.PaymentDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.PaymentRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    CardService cardService;
    @Autowired
    AccountService accountService;
    @Autowired
    PaymentService paymentService;

    @GetMapping("/get-payments")
    public List<PaymentDTO> getPayments() {
        List<Payment> payments = paymentService.getPayments();
        return paymentService.getPaymentsDTO(paymentService.getPayments());
    }

    @Transactional
    @CrossOrigin(origins = "http://127.0.0.1:5501")
    @PostMapping("/receive-payment")
    public ResponseEntity<Object> receivePayment(@RequestParam String cardNumber, @RequestParam Integer cvv,
                                                 @RequestParam Double amount, @RequestParam String description) {
        Card card = cardService.findByNumber(cardNumber);
        if(card == null)
            return new ResponseEntity<>("Card doesn't exist", HttpStatus.FORBIDDEN);
        if (!Objects.equals(card.getCvv(), cvv))
            return new ResponseEntity<>("Wrong card information", HttpStatus.FORBIDDEN);

        Account account = card.getAccount();
        if(account != null) {
            if (account.getBalance() < amount) {
                return new ResponseEntity<>("Not enough balance in account " + account.getNumber(), HttpStatus.FORBIDDEN);
            }
            Payment payment = new Payment(cardNumber, amount, description, cvv);
            payment.setCard(card);
            paymentService.savePayment(payment);
            card.addPayment(payment);
            cardService.save(card);
            Transaction transaction = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now(),
                    account.getBalance() - amount);
            Set<Transaction> transactions = account.getTransactions();
            transactions.add(transaction);
            account.setTransactions(transactions);
            account.setBalance(account.getBalance() - amount);
            accountService.save(account);
            return new ResponseEntity<>("Payment received", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }

    }
}
