package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;

import java.util.List;

public interface CardService {

    public Card getCard(Long id);
    public List<Card> getCards();
    public CardDTO getCardDTO(Card card);
    public List<CardDTO> getCardsDTO(List<Card> cards);
    public void deleteCard(Long id);
    public Card findByNumber(String number);
    public void save(Card card);
}
