package com.yyggee.eggs.model.ds1.ordero;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "products_fulfilled")
public class ProductsFulfilled {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer quantitiesDeliver;
    private Integer quantitiesReject;
}
