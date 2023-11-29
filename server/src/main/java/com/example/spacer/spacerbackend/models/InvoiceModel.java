package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"status", "clientId"})
@Table(name = "factura")
public class InvoiceModel implements Serializable {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  @Column(name = "idfac")
  private Long id;

  @JsonProperty("clientId")
  @Column(name = "idcli")
  private Long clientId;

  @OneToMany(mappedBy = "invoiceId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<ProductInvoiceModel> products;

  @Column(name = "timestamp")
  private Timestamp timestamp;

  @Column(name = "total")
  private Double total;

  @JsonProperty("status")
  @Column(name = "estfac")
  private String status;

  @PrePersist
  public void prePersist() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    this.timestamp = Timestamp.valueOf(sdf.format(new Date()));
  }
}
