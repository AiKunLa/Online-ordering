package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //插入数据
        dishMapper.insert(dish);

        //获取分类id   dishDTO中没有菜品分类id 可以通过上面插入后返回id
        Long categoryId = dish.getCategoryId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                //设置 分类id
                flavor.setDishId(categoryId);
            });
            //将菜品口味批量插入
            dishFlavorMapper.insertBatch(flavors);
        }
        //插入菜品
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishPage = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(dishPage.getTotal(), dishPage.getResult());
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
    }

    /**
     * 菜品起售、停售
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder().id(id).status(status).build();
        dishMapper.update(dish);

    }

//    /**
//     * 根据分类id查询菜品
//     * @param categoryId
//     * @return
//     */
//    @Override
//    public PageResult list(Integer categoryId) {
//        DishPageQueryDTO dishPageQueryDTO = new DishPageQueryDTO();
//        dishPageQueryDTO.setCategoryId(categoryId);
//        Page<DishVO> dishVOS = dishMapper.pageQuery(dishPageQueryDTO);
//        return new PageResult(dishVOS.getTotal(),dishVOS.getResult());
//    }


    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        List<Dish> dishList = dishMapper.getByCategoryId(categoryId);
        return dishList;
    }

    /**
     * 根据id查询菜品
     *
     * @param dishId
     * @return
     */
    @Override
    public DishVO queryById(Long dishId) {
        //查询菜品 查询菜品口味
        //查询菜品
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getById(dishId);
        //查询口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.queryFlavorByDishId(dishId);
        return dishVO;
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断是否启售 启售中的不能被删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否关联套餐 关联了不能删除
        List<Long> setmealIdByDishId = setmealDishMapper.getSetmealIdByDishId(ids);
        if (setmealIdByDishId != null && setmealIdByDishId.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的数据
        dishMapper.delete(ids);

        //删除口味数据
        dishFlavorMapper.deleteByDishId(ids);
    }

    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        //根据分类id查询菜品
        List<DishVO> dishVOList = dishMapper.list(dish);

        //根据菜品id查询菜品口味
        dishVOList.forEach(d -> {
            //通过dishId查询 口味
            List<DishFlavor> dishFlavors = dishFlavorMapper.queryFlavorByDishId(d.getId());
            //将口味赋值给dishVo
            d.setFlavors(dishFlavors);
        });
        return dishVOList;
    }
}
