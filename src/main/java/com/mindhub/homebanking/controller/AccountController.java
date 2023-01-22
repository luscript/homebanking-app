package com.mindhub.homebanking.controller;
import com.lowagie.text.*;
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

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id, @RequestParam("from_date") String from_date,
                                 @RequestParam("thru_date") String thru_date) {
        Account account = accountService.getAccount(id);
        System.out.println(from_date);
        System.out.println(thru_date);
       if(!from_date.isEmpty() && !thru_date.isEmpty()) {
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
           LocalDateTime start_date = LocalDateTime.parse(from_date, formatter);
           LocalDateTime end_date = LocalDateTime.parse(thru_date, formatter);
           List<Transaction> transactions = transactionService.getFilteredTransactions(start_date, end_date, id);
           account.setTransactions(transactions.stream().collect(Collectors.toSet()));
           accountService.save(account);
       }
        Document document = new Document(PageSize.A4);
        document.open();
        document.add(new Paragraph("All transactions"));
        Table table = new Table(3);
        table.setBorderWidth(1);
        table.setBorderColor(new Color(0, 0, 255));
        table.setPadding(5);
        table.setSpacing(5);
        Cell cell = new Cell("header");
        cell.setHeader(true);
        cell.setColspan(3);
        table.addCell(cell);
        table.endHeaders();
        cell = new Cell("example cell with colspan 1 and rowspan 2");
        cell.setRowspan(2);
        cell.setBorderColor(new Color(255, 0, 0));
        table.addCell(cell);
        table.addCell("1.1");
        table.addCell("2.1");
        table.addCell("1.2");
        table.addCell("2.2");
        table.addCell("cell test1");
        cell = new Cell("big cell");
        cell.setRowspan(2);
        cell.setColspan(2);
        table.addCell(cell);
        table.addCell("cell test2");
        document.add(table);
        document.close();

        return accountService.getAccountDTO(account);
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
    public ResponseEntity<Object> disableAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        if(account.getBalance() > 0) {
            return new ResponseEntity<>("Cannot delete accounts that have money", HttpStatus.FORBIDDEN);
        } else {
            account.setEnabled(false);
            Set<Transaction> transactions = account.getTransactions();
            transactions.forEach(transaction -> transactionService.deleteTransaction(transaction.getId()));
            accountService.save(account);
        }
        return new ResponseEntity<>("Erased", HttpStatus.ACCEPTED);
    }


}
