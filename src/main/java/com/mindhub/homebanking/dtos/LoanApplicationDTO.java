package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;

import java.util.ArrayList;
import java.util.List;

public class LoanApplicationDTO {
    private Long id;
    private Double amount;
    private Integer payments;
    private String destinyAccountNumber;

    public LoanApplicationDTO() {}

    public LoanApplicationDTO(Long id, Double amount, Integer payments, String destinyAccountNumber) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.destinyAccountNumber = destinyAccountNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public void setPayments(Integer payments) {
        this.payments = payments;
    }

    public String getDestinyAccountNumber() {
        return destinyAccountNumber;
    }

    public void setDestinyAccountNumber(String destinyAccountNumber) {
        this.destinyAccountNumber = destinyAccountNumber;
    }
}
