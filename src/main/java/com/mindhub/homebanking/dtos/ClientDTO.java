package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class ClientDTO {
    long id;
    private String firstName, lastName, email;
    private Set<Account> accounts;
    private Set<ClientLoan> loans;
    private Set<Card> cards;
    private boolean isAdmin;

    public ClientDTO() {
    }

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts();
        this.loans = client.getClientLoans();
        this.cards = client.getCards();
        this.isAdmin = client.isAdmin();
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts.stream().map(AccountDTO::new).collect(toSet());

    }
    public Set<ClientLoanDTO> getClientLoans() {
        return loans.stream().map(ClientLoanDTO::new).collect(toSet());
    }

    public Set<CardDTO> getCards() {
        return cards.stream().map(CardDTO::new).collect(toSet());
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
