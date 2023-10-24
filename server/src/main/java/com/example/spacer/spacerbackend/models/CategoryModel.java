package com.example.spacer.spacerbackend.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Entity
@Table(name = "categoria")
public class CategoryModel implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="idcat")
  private Long id;

  @lombok.Setter
  @Column(name="nomcat")
  private String name;

  public void setId(Long id) {
    this.id = id;
  }
}
