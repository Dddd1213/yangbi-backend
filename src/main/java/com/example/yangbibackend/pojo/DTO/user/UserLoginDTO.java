package com.example.yangbibackend.pojo.DTO.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户登录数据")
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @ApiModelProperty("账户")
    private String userAccount;

    @ApiModelProperty("密码")
    private String userPassword;
}
