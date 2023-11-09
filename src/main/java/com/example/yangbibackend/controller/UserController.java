package com.example.yangbibackend.controller;

import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.result.Result;
import com.example.yangbibackend.common.utils.ResultUtils;
import com.example.yangbibackend.pojo.DTO.user.UserLoginDTO;
import com.example.yangbibackend.pojo.DTO.user.UserRegisterDTO;
import com.example.yangbibackend.pojo.VO.user.UserLoginVO;
import com.example.yangbibackend.pojo.VO.user.UserRegisterVO;
import com.example.yangbibackend.pojo.entity.User;
import com.example.yangbibackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@Api(tags = "用户相关接口")
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    /**
     *
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value="用户注册接口")
    public Result<UserRegisterVO> userRegister(@RequestBody UserRegisterDTO userRegisterDTO){

        if (userRegisterDTO == null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserRegisterVO userRegisterVO = userService.register(userAccount,userPassword,checkPassword);

        return ResultUtils.success(userRegisterVO);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request){

        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount,userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO userLoginVO = userService.userLogin(userAccount, userPassword, request);

        return ResultUtils.success(userLoginVO);
    }

    @PostMapping("/logout")
    @ApiOperation(value="退出登录")
    public Result<Boolean> userLogout(HttpServletRequest request){

        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.userLogout(request);

        return ResultUtils.success(result);
    }

    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前用户登录信息")
    public Result<UserLoginVO> getLoginUser(HttpServletRequest request){
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO userLoginVO = userService.getLoginUser(request);
        return ResultUtils.success(userLoginVO);
    }



}
