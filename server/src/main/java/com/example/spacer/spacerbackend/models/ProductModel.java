package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "producto")
public class ProductModel implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idpro")
  private Long id;

  @Column(name = "marca")
  private String marca;

  @Column(name = "despro")
  private String description;

  @Column(name = "prepro")
  private double price;

  @ManyToOne
  @JoinColumn(name="idcat")
  private CategoryModel categoryId;

  @Column(name = "stock")
  private int stock;

  @JsonIgnore
  @Column(name = "imgprod")
  private byte[] img;

  @Column(name = "urlprod")
  private String urlImg;

  public String getUrlImg() {
    HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    String urlBase = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
    return urlBase + "/producto/" + this.urlImg + ".jpg";
  }
}
