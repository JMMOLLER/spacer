package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "producto")
public class ProductModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idpro")
  private Long id;

  @lombok.Setter
  private String marca;
  @lombok.Setter
  @Column(name = "despro")
  private String description;
  @lombok.Setter
  @Column(name = "prepro")
  private double price;

  @lombok.Setter
  @ManyToOne
  @JoinColumn(name="idcat")
  private CategoryModel categoryId;

  @lombok.Setter
  @Column(name = "stock")
  private int stock;
  @lombok.Setter
  @JsonIgnore
  @Column(name = "imgprod")
  private byte[] img;
  @lombok.Setter
  @Column(name = "urlprod")
  private String urlImg;

}
