package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品id查询对象套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdByDishId(List<Long> ids);
    /**
     * 插入数据
     * @param setmealDishes
     */
    void save(List<SetmealDish> setmealDishes);


    /**
     * 批量删除套餐 相关的菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 通过id删除套餐-菜品数据
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId} ")
    void deleteById(Long setmealId);


    @Select("select dish_id from setmeal_dish where setmeal_id = #{id} ")
    List<Long> getDishIdBySetmealId(Long id);
}
