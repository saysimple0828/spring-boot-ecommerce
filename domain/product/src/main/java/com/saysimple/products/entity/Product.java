package com.saysimple.products.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "product")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String productId;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 50)
    private String categoryId;
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
//    @JoinColumn(name = "option_id")
//    private List<OptionEntity> options = new ArrayList<>();
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
//    @JoinColumn(name = "info_id")
//    private List<InfoEntity> infos = new ArrayList<>();
}
