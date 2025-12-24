package com.yyggee.eggs.model.ds1.ordero;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private Integer quantity;
  private Long price;
  private Long totalPrice;
  private String currency;
  private String sku;
}
