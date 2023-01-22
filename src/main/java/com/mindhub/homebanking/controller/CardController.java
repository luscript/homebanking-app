package com.mindhub.homebanking.controller;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardService cardService;

    @Autowired
    ClientService clientService;

    @Autowired
    AccountService accountService;

    @RequestMapping("/cards/{id}")
    public CardDTO getCard(@PathVariable Long id) {
        return cardService.getCardDTO(cardService.getCard(id));
    }
    @RequestMapping("/cards")
    public List<CardDTO> getCards() {
        return cardService.getCardsDTO(cardService.getCards());
    }

    @DeleteMapping("/clients/current/cards")
    public void deleteCard(@RequestParam Long id) {
        cardService.deleteCard(id);
    }

    public String getRandomNumber(int min, int max) {
        Random random = new Random();
        String number = random.nextInt(max - min) + min + "-" + random.nextInt(max - min) + min + "-" +
                random.nextInt(max - min) + min + "-" + random.nextInt(max - min) + min;
        String finalNumber = number;
        Set<String> cardsNumbers = cardService.getCards().stream().map(card -> card.getNumber())
                .filter(cardNumber -> cardNumber == finalNumber).collect(Collectors.toSet());
        if(cardsNumbers.size() > 0) {
            number = getRandomNumber(0000, 9999);
        }
        return number;
    }

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> register (
            @RequestParam CardColor color, @RequestParam CardType type, @RequestParam String accountNumber,
            Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());
        Set<Card> filteredCards = client.getCards().stream().filter(card -> card.getColor() == color && card.getType() == type).collect(Collectors.toSet());

        if(filteredCards.size() > 0) {
            return new ResponseEntity<>("Cards limit exceeded", HttpStatus.FORBIDDEN);
        }

        if (color.toString().isEmpty() || type.toString().isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        String number = getRandomNumber(0000,9999);
        Random random = new Random();
        int cvv = random.nextInt(999-111) + 111;
        Card card = new Card(client.getFirstName() + " " + client.getLastName(), number, cvv,
                LocalDate.now(), LocalDate.now().plusYears(5), type, color);
        cardService.save(card);
        client.addCard(card);
        clientService.save(client);
       if(type.equals(CardType.DEBIT)) {
           Account account = accountService.findByNumber(accountNumber);
           account.setCard(card);
           accountService.save(account);
       }
        return new ResponseEntity<>(HttpStatus.CREATED);
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
            Transaction transaction = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now(),
                    account.getBalance()-amount);
            Set<Transaction> transactions = account.getTransactions();
            transactions.add(transaction);
            account.setTransactions(transactions);
            account.setBalance(account.getBalance()-amount);
            accountService.save(account);
        }
        return new ResponseEntity<>("Payment received", HttpStatus.ACCEPTED);
    }

}
