package com.sky.mapper;

import com.sky.entity.ShoppingCart;
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
     * 查看购物车
     * @return
     */
    @Select("select * from shopping_cart")
    List<ShoppingCart> listAll();

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
}
