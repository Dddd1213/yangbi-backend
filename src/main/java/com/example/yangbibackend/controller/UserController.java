package com.example.yangbibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.result.Result;
import com.example.yangbibackend.common.utils.ResultUtils;
import com.example.yangbibackend.pojo.DTO.user.UserLoginDTO;
import com.example.yangbibackend.pojo.DTO.user.UserRegisterDTO;
import com.example.yangbibackend.pojo.VO.user.UserLoginVO;
import com.example.yangbibackend.pojo.VO.user.UserRegisterVO;
import com.example.yangbibackend.pojo.entity.Chart;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    /**
     *用户注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
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

    /**
     * 用户登录
     * @param userLoginDTO
     * @param request
     * @return
     */
    @PostMapping("/login")
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

    /**
     * 用户登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request){

        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.userLogout(request);

        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public Result<UserLoginVO> getLoginUser(HttpServletRequest request){
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO userLoginVO = userService.getLoginUser(request);
        return ResultUtils.success(userLoginVO);
    }

//    /**
//     * 分页获取当前用户创建的资源列表
//     * @param chartQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/my/list/page")
//    public Result<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
//                                                 HttpServletRequest request)



}
