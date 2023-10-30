package com.example.spacer.spacerbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "contraseñas")
public class PasswordResetModel implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name="idcon")
  private String id;

  @Column(name="idcli")
  private Long clientId;

  private String timestamp;
}
