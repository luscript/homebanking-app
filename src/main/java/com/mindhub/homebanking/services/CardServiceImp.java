package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardServiceImp implements CardService{

    @Autowired
    CardRepository cardRepository;

    @Override
    public Card getCard(Long id) {
        Optional<Card> card = cardRepository.findById(id);
        if(card.isPresent()) {
            return card.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Card> getCards() {
        return cardRepository.findAll();
    }

    @Override
    public CardDTO getCardDTO(Card card) {
        return new CardDTO(card);
    }

    @Override
    public List<CardDTO> getCardsDTO(List<Card> cards) {
        return cards
                .stream()
                .map(CardDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Card card) {
        cardRepository.save(card);
    }
}
