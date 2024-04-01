package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    @Transactional
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //查询购物车里是否有该菜品或套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //设置当前用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByCondition(shoppingCart);

        //如果有 数量加一
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
            return;
        }

        //如果没有则插入

        //插入的是菜品
        //1.根据菜品id获取相关菜品信息 如图片 名字 口味
        //2.插入
        if (shoppingCart.getDishId() != null) {
            Long dishId = shoppingCart.getDishId();

            Dish dish = dishMapper.getById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());

        } else if (shoppingCart.getSetmealId() != null) {
            Long setmealId = shoppingCart.getSetmealId();
            Setmeal setmeal = setmealMapper.getById(setmealId);

            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        } else {
            throw new RuntimeException("请添加商品！！！");
        }

        //插入的是套餐
        //1.根据套餐id查询 相关套餐信息

        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setNumber(1);
        shoppingCartMapper.insert(shoppingCart);

    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> listShopping() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.queryByCondition(shoppingCart);
    }


    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        shoppingCartMapper.clean();
    }

    /**
     * 删除购物车中一个商品
     *
     * @param shoppingCartDTO
     */
    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {
        //查询 该商品数量

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //查询商品
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryByCondition(shoppingCart);
        shoppingCart = shoppingCarts.get(0);
        //判断数量
        if (shoppingCart.getNumber() > 1){
            //数量大于一 则数量减一
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            //更新购物车
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else {
            //数量为一直接删除
            shoppingCartMapper.deleteOneBYCondition(shoppingCart);
        }
    }
}
