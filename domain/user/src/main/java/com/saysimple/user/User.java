package com.saysimple.user;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;

@Data
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, length = 50, unique = true)
    private String phoneNumber;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = true)
    private String encryptedPwd;
    @Column(nullable = false)
    @ColumnDefault("true") // default
    private Boolean isActive;
    @Column(length = 50)
    private String profileImage;
}
