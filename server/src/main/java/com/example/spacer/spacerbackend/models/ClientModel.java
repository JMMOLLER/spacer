package com.example.spacer.spacerbackend.models;

import jakarta.persistence.*;
import lombok.Getter;

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
  private String password;
  @lombok.Setter
  private String email;
  @lombok.Setter
  private String username;

  public void setId(Long id) {
    this.id = id;
  }

}