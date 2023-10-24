package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Entity
@Table(name = "carrito")
public class CartModel implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idcar")
  private Long id;

  @lombok.Setter
  @ManyToOne
  @JoinColumn(name="idcli")
  @JsonBackReference
  private ClientModel clientId;

  @lombok.Setter
  @ManyToOne
  @JoinColumn(name="idpro")
  private ProductModel productId;

  @lombok.Setter
  @Column(name = "cant")
  private Integer quantity;

  public void setId(Long id) {
    this.id = id;
  }

}
