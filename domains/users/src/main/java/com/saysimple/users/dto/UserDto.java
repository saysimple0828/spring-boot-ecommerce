package com.saysimple.users.dto;

import com.saysimple.users.vo.ResponseOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private String email;
    private String name;
    private String pwd;
    private String userId;
    private String profileImage;
    private String encryptedPwd;
    private Date createdAt;

    private List<ResponseOrder> orders;
}
