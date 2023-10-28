package com.example.spacer.spacerbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "password_reset")
public class PasswordResetModel implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name="idpwdres")
  private String id;

  @Column(name="idcli")
  private Long clientId;

  private String timestamp;
}
