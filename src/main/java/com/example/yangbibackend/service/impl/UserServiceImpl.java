package com.example.yangbibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.constant.PasswordConstant;
import com.example.yangbibackend.common.constant.UserConstant;
import com.example.yangbibackend.mapper.UserMapper;
import com.example.yangbibackend.pojo.VO.user.UserLoginVO;
import com.example.yangbibackend.pojo.VO.user.UserRegisterVO;
import com.example.yangbibackend.pojo.entity.User;
import com.example.yangbibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static com.example.yangbibackend.common.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 31067
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-11-08 16:08:43
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{


    /**
     * 注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public UserRegisterVO register(String userAccount, String userPassword, String checkPassword) {

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            userPassword = DigestUtils.md5DigestAsHex((userPassword+PasswordConstant.PASSWORD_SALT).getBytes(StandardCharsets.UTF_8));
            // 3. 插入数据
            User user = User.builder()
                    .userAccount(userAccount)
                    .userPassword(userPassword)
                    .build();
            boolean save = this.save(user);
            if(!save){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库异常操作失败");
            }
            UserRegisterVO userRegisterVO = UserRegisterVO.builder()
                    .id(user.getId())
                    .build();
            return userRegisterVO;
        }
    }

    /**
     * 登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public UserLoginVO userLogin(String userAccount,String userPassword,HttpServletRequest request) {

        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        userPassword = DigestUtils.md5DigestAsHex((userPassword+ PasswordConstant.PASSWORD_SALT).getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, userPassword);
        User user = baseMapper.selectOne(queryWrapper);

        if(user==null)
        {
            log.info("user login failed, userAccount cannot match userPassword",userAccount,userPassword);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        BeanUtils.copyProperties(user,userLoginVO);

        //记录登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);

        return userLoginVO;
    }
    /**
     * 登出
     * @param request
     * @return
     */
    @Override
    public Boolean userLogout(HttpServletRequest request) {
        if(request.getSession().getAttribute(USER_LOGIN_STATE)==null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"没有用户的登录信息");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserLoginVO getLoginUser(HttpServletRequest request) {

        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);

        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        BeanUtils.copyProperties(user,userLoginVO);
        return userLoginVO;
    }

}




