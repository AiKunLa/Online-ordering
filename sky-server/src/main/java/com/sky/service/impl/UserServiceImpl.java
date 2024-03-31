package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空则登录失败，抛出异常
        if (openid == null)
            //登录失败
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        //判断是否为新用户
        User user = userMapper.selectByOpenid(openid);

        //若为新用户，则完成自动注册
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回对象
        return user;
    }

    /**
     * 获取并解析 openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        //调用微信接口服务 获得当前微信用户的openid
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appid",weChatProperties.getAppid());
        hashMap.put("secret",weChatProperties.getSecret());
        hashMap.put("js_code",code);
        hashMap.put("grant_type","authorization_code");

        //网址 加 数据
        String jsonData = HttpClientUtil.doGet(WX_LOGIN, hashMap);

        //解析
        JSONObject jsonObject = JSON.parseObject(jsonData);
        return jsonObject.getString("openid");
    }

}
