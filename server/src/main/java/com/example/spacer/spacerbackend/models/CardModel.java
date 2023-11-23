package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tarjetas")
public class CardModel implements Serializable {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  @Column(name = "idtar")
  private Long id;

  @Column(name = "numtar")
  private Long cardNumber;

  @Column(name = "titular")
  private String cardHolder;

  @Column(name = "vencimiento")
  private LocalDate expirationDate;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "cvv")
  private Short cvv;

}
