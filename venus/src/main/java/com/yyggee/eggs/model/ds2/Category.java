package com.yyggee.eggs.model.ds2;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "payopt_type")
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;
}

