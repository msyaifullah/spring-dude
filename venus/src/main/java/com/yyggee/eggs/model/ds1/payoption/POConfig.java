package com.yyggee.eggs.model.ds1.payoption;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class POConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String currency;
  private String order;
}
