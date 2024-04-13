package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid} ")
    User selectByOpenid(String openid);

    //插入用户数据 并返回自增主键
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time) " +
            "values (#{openid} ,#{name} ,#{phone} ,#{sex} ,#{idNumber} ,#{avatar} ,#{createTime} )")
    void insert(User user);

    @Select("select * from orders where user_id=#{userId} ")
    User getById(Long userId);

    @Select("select count(id) from user where create_time between #{beginTime} and #{endTime}")
    Integer newUserNum(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(id) from user where create_time <= #{endTime} ")
    Integer totalUserNum(LocalDateTime endTime);
}
