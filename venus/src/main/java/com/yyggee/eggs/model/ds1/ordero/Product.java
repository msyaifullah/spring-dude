package com.yyggee.eggs.model.ds1.ordero;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "product")
public class Product{
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
