package com.example.spacer.spacerbackend.models;

import com.example.spacer.spacerbackend.repositories.ClientRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Entity
@Table(name = "cliente")
public class ClientModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="idcli")
  private Long id;

  @lombok.Setter
  @Column(name="nomcli")
  private String firstName;
  @lombok.Setter
  @Column(name="apecli")
  private String lastName;
  @lombok.Setter
  @Column(name="imgcli")
  @JsonIgnore
  private byte[] img;
  @lombok.Setter
  @Column(name="urlcli")
  private String url_img;
  @lombok.Setter
  @Column(name="rolcli")
  private Integer rol;
  @lombok.Setter
  @Column(name="dircli")
  private String address;
  @lombok.Setter
  @Column(name="crdcli")
  private Integer cardNumber;

  @lombok.Setter
  @OneToMany(mappedBy = "clientId", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<CartModel> cart;

  @lombok.Setter
  private String password;
  @lombok.Setter
  private String email;
  @lombok.Setter
  private String username;

  public void setId(Long id) {
    this.id = id;
  }
}