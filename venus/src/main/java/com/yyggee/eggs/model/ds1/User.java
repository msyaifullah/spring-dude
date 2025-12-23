package com.yyggee.eggs.model.ds1;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 4, message = "Minimum password length: 4 characters")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles")
    List<Role> roles;

    @Column(unique = true)
    private String session;

}

