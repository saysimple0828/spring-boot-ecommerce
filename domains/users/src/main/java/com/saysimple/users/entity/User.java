package com.saysimple.users.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String encryptedPwd;
    @Column(nullable = false)
    @ColumnDefault("true") // default
    private Boolean isActive;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(length = 50)
    private String profileImage;
}
