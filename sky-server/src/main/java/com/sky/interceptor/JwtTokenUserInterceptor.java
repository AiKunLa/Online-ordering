package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        //如果不是动态方法直接放行
        if (!(handler instanceof HandlerMethod)) return true;

        //获取请求头中的jwt令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        log.info("校验jwt令牌：{}", token);
        //校验令牌
        try {
            //通过放行
            //解析token
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            //获取用户id 储存到线程中
//            Long userId = Long.parseLong(claims.get(JwtClaimsConstant.USER_ID).toString());
//            BaseContext.setCurrentId(userId);

            return true;
        } catch (Exception e) {
            //不通过 响应401异常
            response.setStatus(401);
            return false;
        }
    }
}
