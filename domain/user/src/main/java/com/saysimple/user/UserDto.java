package com.saysimple.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private long id;
    private String userId;
    private String phoneNumber;
    private String name;
    private String pwd;
    private String encryptedPwd;
    private String profileImage;
    private Date createdAt;
}
