package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    //jwt 属性配置类 （配置了jwt的相关属性）
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "微信登录")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信登录：{}", userLoginDTO);

        //匹配是否有该用户 或者注册
        User user = userService.wxLogin(userLoginDTO);

        //登录成功后jwt令牌
        HashMap<String, Object> claims = new HashMap<>();
        //
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        //key 生效时长 数据（Map）
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(), claims);

        //创建返回给客户端的对象
        UserLoginVO userLoginVO = UserLoginVO.builder().id(user.getId())
                .token(jwt).openid(user.getOpenid()).build();

        return Result.success(userLoginVO);
    }
}
