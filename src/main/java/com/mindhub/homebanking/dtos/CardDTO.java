package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;

import java.time.LocalDate;

public class CardDTO {
    private Long id;
    private String cardholder;
    private String number;
    private Integer cvv;
    private LocalDate fromDate, thruDate;
    private CardType type;
    private CardColor color;
    public CardDTO() {}
    public CardDTO(Card card) {
        this.id = card.getId();
        this.cardholder = card.getCardholder();
        this.number = card.getNumber();
        this.cvv = card.getCvv();
        this.fromDate = card.getFromDate();
        this.thruDate = card.getThruDate();
        this.type = card.getType();
        this.color = card.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getCardholder() {
        return cardholder;
    }

    public String getNumber() {
        return number;
    }

    public Integer getCvv() {
        return cvv;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public CardType getType() {
        return type;
    }

    public CardColor getColor() {
        return color;
    }
}
