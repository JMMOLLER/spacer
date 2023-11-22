package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.CardModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends CrudRepository<CardModel, Long> {
}
