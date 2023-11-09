package com.example.yangbibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yangbibackend.pojo.DTO.user.UserLoginDTO;
import com.example.yangbibackend.pojo.VO.user.UserLoginVO;
import com.example.yangbibackend.pojo.VO.user.UserRegisterVO;
import com.example.yangbibackend.pojo.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author 31067
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-11-08 16:08:43
*/
public interface UserService extends IService<User>{

    UserLoginVO userLogin(String userAccount,String userPassword, HttpServletRequest request);

    UserRegisterVO register(String userAccount, String userPassword, String checkPassword);

    Boolean userLogout(HttpServletRequest request);

    UserLoginVO getLoginUser(HttpServletRequest request);
}
