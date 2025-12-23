package com.yyggee.eggs.model.ds1.ordero;

import lombok.Data;

import jakarta.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "ordero")
public class Ordero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Boolean acceptPartial;
    private String invoiceRefNum;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ordero_id", referencedColumnName = "id")
    private Set<Product> products;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ordero_id", referencedColumnName = "id")
    private Set<ProductsFulfilled> productsFulfilled;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "buyer", referencedColumnName = "id")
    private Person buyer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "recipient", referencedColumnName = "id")
    private Person recipient;

}
