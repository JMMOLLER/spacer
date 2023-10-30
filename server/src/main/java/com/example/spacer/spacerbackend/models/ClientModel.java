package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@JsonIgnoreProperties({"new-password"})
@Table(name = "cliente")
public class ClientModel implements Serializable {

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
  @Transient
  private String urlImg;

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
  @Transient
  @JsonProperty("new-password")
  private String newPassword;
  @lombok.Setter
  private String password;
  @lombok.Setter
  private String email;
  @lombok.Setter
  private String username;
  public void setId(Long id) {
    this.id = id;
  }

  // This method is used to get the url of the image of the client - not must be deleted
  public String getUrlImg() {
    HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    String urlBase = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
    return urlBase + "/cliente/" + this.username + ".jpg";
  }

}