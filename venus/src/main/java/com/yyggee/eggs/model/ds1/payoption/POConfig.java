package com.yyggee.eggs.model.ds1.payoption;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class POConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String currency;
    private String order;

}
