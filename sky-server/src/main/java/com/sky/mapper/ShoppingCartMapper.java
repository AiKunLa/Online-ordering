package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 购物车查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> queryByCondition(ShoppingCart shoppingCart);

    /**
     * 更新商品数据
     * @param shoppingCart
     */
    void update(ShoppingCart shoppingCart);

    /**
     * 通过id更新商品数量
     * @param cart
     */
    @Update("update shopping_cart set number = #{number} where id=#{id} ")
    void updateNumberById(ShoppingCart cart);

    /**
     * 插入商品数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor,number, amount, create_time) " +
            "values (#{name} ,#{image} ,#{userId} ,#{dishId} ,#{setmealId} ,#{dishFlavor} ,#{number} ,#{amount}  ,#{createTime} );")
    void insert(ShoppingCart shoppingCart);


    /**
     * 清空购物车
     */
    @Delete("delete from shopping_cart")
    void clean();

    /**
     * 删除购物车中一个商品
     * @param shoppingCart
     */
    void deleteOneBYCondition(ShoppingCart shoppingCart);

    /**
     * 通过用户id获取购物车商品
     * @param currentId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{currentId} ")
    List<ShoppingCart> getByUserId(Long currentId);

    /**
     * 查询当前用户购物车信息
     * @param shoppingCart
     * @return
     */
    @Select("select * from shopping_cart where user_id=#{userId} ")
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 清除数据
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId} ")
    void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
