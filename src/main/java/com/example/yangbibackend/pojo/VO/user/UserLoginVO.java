package com.example.yangbibackend.pojo.VO.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class UserLoginVO implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
