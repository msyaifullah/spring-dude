package com.yyggee.eggs.model.ds1.ordero;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String email;
    private String phone;
}
