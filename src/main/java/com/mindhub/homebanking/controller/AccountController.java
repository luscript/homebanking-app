package com.mindhub.homebanking.controller;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//Escucha y responde las peticiones (requests) del cliente. Maneja las direcciones de las URL

//MC -> Arquitectura modelo controlador -> "devuelve" html
//Rest controller -> devuelve JSON o XML

@RestController
@RequestMapping("/api") /**/
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    TransactionService transactionService;

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
       return accountService.getAccountDTO(accountService.getAccount(id));
    }
    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountService.getAccountsDTO(accountService.getAccounts());
    }

    public String getRandomNumber(int min, int max) {
        Random random = new Random();
        String number = "VIN" + random.nextInt(max - min) + min;
        String finalNumber = number;
        Set<String> accountsNumbers =   accountService.getAccounts().stream().map(account -> account.getNumber())
                .filter(accountNumber -> accountNumber == finalNumber).collect(Collectors.toSet());
        if(accountsNumbers.size() > 0) {
            number = getRandomNumber(0000, 9999);
        }
        return number;
    }
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> register(Authentication authentication, @RequestParam AccountType accountType) {
        Client client = clientService.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts().stream().filter(account -> account.isEnabled() == true).collect(Collectors.toSet());
        if (accounts.size() > 2) {
            return new ResponseEntity<>("Accounts limit exceeded", HttpStatus.FORBIDDEN);
        }
        String accountNumber = getRandomNumber(0000,9999);
        Account account = new Account(accountNumber, LocalDateTime.now(), 0.00, true, accountType);
        accountService.save(account);
        client.addAccount(account);
        clientService.save(client);
        return new ResponseEntity<>("created ",HttpStatus.CREATED);
    }

    @PatchMapping("/clients/current/accounts/{id}")
    public void disableAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        account.setEnabled(false);
        Set<Transaction> transactions = account.getTransactions();
        transactions.forEach(transaction -> transactionService.deleteTransaction(transaction.getId()));
        accountService.save(account);
    }


}
