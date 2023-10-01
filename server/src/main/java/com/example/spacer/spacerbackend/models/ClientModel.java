package com.example.spacer.spacerbackend.models;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "cliente")
public class ClientModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @lombok.Setter
  private String nomcli;
  @lombok.Setter
  private String apecli;
  @lombok.Setter
  private String password;
  @lombok.Setter
  private Integer rolcli;
  @lombok.Setter
  private String dircli;
  @lombok.Setter
  private Integer crdcli;
  @lombok.Setter
  private String email;
  @lombok.Setter
  private String username;
  @lombok.Setter
  private byte[] img;
  @lombok.Setter
  private String img_cli;

  public void setId(Long id) {
    this.id = id;
  }

}