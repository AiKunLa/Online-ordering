package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 菜品口味批量插入
     * @param flavors
     */
    void insertBatch(@Param(value = "flavors") List<DishFlavor> flavors);

    /**
     * 通过dish id删除口味
     * @param dishId
     */
    void deleteByDishId(List<Long> dishId);
}
