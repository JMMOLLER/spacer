package com.example.spacer.spacerbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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

  @Column(name="timestamp", nullable = false)
  private Timestamp timestamp;

  @PrePersist
  public void prePersist() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    this.timestamp = Timestamp.valueOf(sdf.format(new Date()));
  }
}
