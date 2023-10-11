package com.example.spacer.spacerbackend.models;

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
  // @Column(name="marca")
  private String marca;
  @lombok.Setter
  @Column(name = "despro")
  private String description;
  @lombok.Setter
  @Column(name = "prepro")
  private String price;
  @lombok.Setter
  @Column(name = "idcat")
  private Integer categoryId;
  @lombok.Setter
  @Column(name = "stock")
  private String stock;
  @lombok.Setter
  @Column(name = "imgprod")
  private byte[] img;
  @lombok.Setter
  @Column(name = "urlprod")
  private String urlImg;

  public void setId(Long id) {
    this.id = id;
  }
}
