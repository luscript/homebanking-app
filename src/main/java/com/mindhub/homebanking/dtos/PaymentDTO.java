package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Payment;

public class PaymentDTO {
    private Long id;
    private String cardNumber;
    private Integer cvv;
    private Double amount;
    private String description;
    private Card card;

    public PaymentDTO() {}
    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.cardNumber = payment.getCardNumber();
        this.cvv = payment.getCvv();
        this.amount = payment.getAmount();
        this.description = payment.getDescription();
        this.card = payment.getCard();
    }

    public Long getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Integer getCvv() {
        return cvv;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public CardDTO getCardDTO() {
        return new CardDTO(this.card);
    }
}
