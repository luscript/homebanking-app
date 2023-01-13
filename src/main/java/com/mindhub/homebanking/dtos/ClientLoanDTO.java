package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

import java.time.LocalDateTime;

public class ClientLoanDTO {
    private Long clientLoanID, loanID;
    private String name;
    private Double amount;
    private Integer payments;
    private LocalDateTime date;

    public ClientLoanDTO() {}
    public ClientLoanDTO(ClientLoan client) {
        this.clientLoanID = client.getId();
        this.loanID = client.getLoan().getId();
        this.name = client.getLoan().getName();
        this.amount = client.getAmount();
        this.payments = client.getPayments();
        this.date = client.getDate();
    }

    public Long getClientLoanID() {
        return clientLoanID;
    }

    public Long getLoanID() {
        return loanID;
    }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
