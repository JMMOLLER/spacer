package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"id"})
@Table(name = "factura_producto")
public class ProductInvoiceModel implements Serializable {
  @Id
  @JsonProperty("id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idfacprod")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "idfac")
  @JsonBackReference
  private InvoiceModel invoiceId;

  @ManyToOne
  @JsonProperty("product")
  @JoinColumn(name = "idpro")
  private ProductModel productId;

  @Column(name = "cantidad")
  private Integer quantity;
}
