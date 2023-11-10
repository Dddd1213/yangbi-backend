package com.example.yangbibackend.interceptor;

import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.yangbibackend.common.constant.UserConstant.USER_LOGIN_STATE;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("进入登录拦截器....");
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        log.info("开始校验是否登录...");
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        log.info("当前用户id:{}", user.getId());
        return true;
    }
}
