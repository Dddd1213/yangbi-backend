package com.example.yangbibackend.pojo.VO.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserRegisterVO implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private Long id;
}
