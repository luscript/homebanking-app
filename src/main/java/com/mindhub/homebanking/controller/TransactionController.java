package com.mindhub.homebanking.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;


    @RequestMapping("/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionService.getTransactionsDTO(transactionService.getTransactions());
    }

/*    @GetMapping("/clients/current/transactions/{id}")
    public List<TransactionDTO> getFilteredTransactions(@PathVariable Long id, @RequestParam("from_date") String from_date,
                                                        @RequestParam("thru_date") String thru_date) {
        LocalDateTime start_date = LocalDateTime.parse(from_date, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end_date = LocalDateTime.parse(thru_date, DateTimeFormatter.ISO_DATE_TIME);
        Account account = accountService.getAccount(id);
        return transactionService.getTransactionsDTO(transactionService.getFilteredTransactions(start_date, end_date));
    }*/


    @Transactional
    @RequestMapping(path = "/clients/current/transactions", method = RequestMethod.POST)
    ResponseEntity<Object> register(Authentication authentication, @RequestParam Double amount,
                                    @RequestParam String description, @RequestParam String originNumber,
                                    @RequestParam String destinyNumber) {

        if(amount.isNaN() || description.isEmpty() || originNumber.isEmpty() || destinyNumber.isEmpty()) {
            return new ResponseEntity<>("Fields cannot be empty", HttpStatus.FORBIDDEN);
        }

        if(originNumber == destinyNumber) {
            return new ResponseEntity<>("accounts cannot be the same", HttpStatus.FORBIDDEN);
        }

        Account originAccount = accountService.findByNumber(originNumber);
        Account destinyAccount = accountService.findByNumber(destinyNumber);

        if(originAccount == null ||destinyAccount == null) {
            return new ResponseEntity<>("Account/s not found", HttpStatus.FORBIDDEN);
        }

        Client client = clientService.findByEmail(authentication.getName());

        if(client == null) {
            return new ResponseEntity<>("user doesn't exist", HttpStatus.FORBIDDEN);
        }

        if(!client.getAccounts().contains(originAccount)) {
            return new ResponseEntity<>("origin account doesnt belong to the authenticated user", HttpStatus.FORBIDDEN);
        }

        if(originAccount.getBalance() < amount) {
            return new ResponseEntity<>("not enough founds", HttpStatus.FORBIDDEN);
        }

        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now(), originAccount.getBalance()-amount);
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now(), destinyAccount.getBalance()+amount);


        debitTransaction.setDescription(description + " " + destinyNumber);
        creditTransaction.setDescription(description + " " + originNumber);

        originAccount.addTransaction(debitTransaction);
        originAccount.setBalance(originAccount.getBalance() - amount);

        destinyAccount.addTransaction(creditTransaction);
        destinyAccount.setBalance(destinyAccount.getBalance() + amount);

        transactionService.save(debitTransaction);
        transactionService.save(creditTransaction);

        accountService.save(originAccount);
        accountService.save(destinyAccount);

        return new ResponseEntity<>("created", HttpStatus.CREATED);
    }


    private ByteArrayInputStream createPdf(Set<Transaction> transactions) throws IOException {
        // Creating a PDF document
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Creating a table
        Table table = new Table(5);
        table.setWidth(100);
        table.setBorderWidth(1);
        table.setPadding(5);

        // Adding cells to the table
        table.addCell("Type");
        table.addCell("Amount");
        table.addCell("Description");
        table.addCell("Date");
        table.addCell("Remaining balance");
        transactions.forEach(transaction -> {
            table.addCell(transaction.getType().toString());
            table.addCell(transaction.getAmount().toString());
            table.addCell(transaction.getDescription());
            table.addCell(transaction.getDate().toString());
            table.addCell(transaction.getRemainingBalance().toString());
        });

        document.add(table);

        // Closing the document
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    @PostMapping(value = "/download-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadPdf(@RequestBody Set<Transaction> transactions) throws IOException {
        ByteArrayInputStream bis = createPdf(transactions); // call the method that creates the pdf

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=table.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
