package com.saysimple.reviews.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@DynamicInsert
@Table(name = "review")
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reviewId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private String userId;

    private String title;

    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer rating;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @LastModifiedBy
    private String modifiedBy;
}
