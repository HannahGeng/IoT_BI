package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResultVO implements Serializable{

    private Boolean loginSuccess;

    private Integer adminId;

    private String token;
}
