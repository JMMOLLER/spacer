package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties({"new-password", "password"})
@Table(name = "cliente")
public class ClientModel implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="idcli")
  private Long id;

  @Column(name="nomcli")
  private String firstName;

  @Column(name="apecli")
  private String lastName;

  @Column(name="imgcli")
  @JsonIgnore
  private byte[] img;

  @Transient
  private String urlImg;

  @Column(name="rolcli")
  private Integer rol;

  @Column(name="dircli")
  private String address;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name="idtar")
  @JsonProperty("cardInfo")
  private CardModel cardId;

  @OneToMany(mappedBy = "clientId", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<CartModel> cart;

  @Transient
  @JsonProperty("new-password")
  private String newPassword;

  @Column(name="password")
  @JsonProperty("password")
  private String password;

  @Column(name="email")
  private String email;

  @Column(name="username")
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