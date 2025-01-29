package com.saysimple.users.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUserDetails extends ResponseUser {
    private Integer money;
    private Integer point;
    private Integer reviewCount;
    private Integer favoriteDecoCount;
    private Integer recentDecoCount;
    private Integer frequentDecoCount;
    private List<ResponseOrder> orders;
}
