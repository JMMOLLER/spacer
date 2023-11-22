package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CardModel;
import com.example.spacer.spacerbackend.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
  CardRepository cardRepository;

  @Autowired
  public CardService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  public void deleteCard(Long id) {
    this.cardRepository.deleteById(id);
  }

  public CardModel updateCard(CardModel card) {
    return this.cardRepository.save(card);
  }

  public CardModel newCard(CardModel card) {
    return this.cardRepository.save(card);
  }
}
