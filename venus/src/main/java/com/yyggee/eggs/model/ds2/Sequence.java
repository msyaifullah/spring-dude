package com.yyggee.eggs.model.ds2;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "payopt_sequence")
public class Sequence implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_name", nullable = false)
    private String configName;

    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @Column(name = "expand", nullable = false)
    private boolean expand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "method_id", nullable = false)
    private Long methodId;

    @Column(name = "status", nullable = false)
    private boolean status;

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

