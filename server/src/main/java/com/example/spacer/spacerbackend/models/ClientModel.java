package com.example.spacer.spacerbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class ClientModel {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nomcli;
  private String apecli;
  private String email;
  private String password;
  private Integer rolcli;
  private String dircli;
  private Integer crdcli;
  private String username;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNomcli() {
    return nomcli;
  }

  public void setNomcli(String nomcli) {
    this.nomcli = nomcli;
  }

  public String getApecli() {
    return apecli;
  }

  public void setApecli(String apecli) {
    this.apecli = apecli;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getRolcli() {
    return rolcli;
  }

  public void setRolcli(Integer rolcli) {
    this.rolcli = rolcli;
  }

  public String getDircli() {
    return dircli;
  }

  public void setDircli(String dircli) {
    this.dircli = dircli;
  }

  public Integer getCrdcli() {
    return crdcli;
  }

  public void setCrdcli(Integer crdcli) {
    this.crdcli = crdcli;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}