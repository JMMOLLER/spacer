package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"cardNumber", "cvv"})
@Table(name = "tarjetas")
public class CardModel {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  @Column(name = "idtar")
  private Long id;

  @JsonProperty("cardNumber")
  @Column(name = "numtar")
  private Long cardNumber;

  @Column(name = "titular")
  private String cardHolder;

  @Column(name = "vencimiento")
  private String expirationDate;

  @JsonProperty("cvv")
  @Column(name = "cvv")
  private Short cvv;

}
