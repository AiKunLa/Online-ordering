package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        //属性拷贝 设置默认状态
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //储存 通过mybatis获取生成的套餐id
        setmealMapper.save(setmeal);
        //对关联的菜品 设置套餐id
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            //设置菜品关联的套餐id
            setmealDish.setSetmealId(setmealId);
        }
        //批量插入 setmealDishs
        setmealDishMapper.save(setmealDishes);
    }

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //使用PageHelper 自动补全 limit 后的页码 和每页的条数
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        //调用dao查询
        Page<SetmealVO> setmealPage = setmealMapper.pageQuery(setmealPageQueryDTO);
        //返回封装的数据
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //启售中的不能删除
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == 1) throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        });
        //删除套餐 删除套餐_菜品表里的相关数据
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBatch(ids);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        //创建套餐对象 将属性拷贝进入该对象
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改套餐
        setmealMapper.update(setmeal);

        //修改套餐_菜品表中关联的菜品套餐数据
        //1.获取套餐id
        Long setmealId = setmeal.getId();
        //2.删除套餐_菜品表中关联的数据
        setmealDishMapper.deleteById(setmealId);
        //3.重新添加数据

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //设置套餐id
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.save(setmealDishes);
    }

    /**
     * 设置套餐启售、停售
     *
     * @param status
     * @param id
     */
    @Transactional
    @Override
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，如果套餐内包含停售的菜品，则不能起售
        //1.起售
        //查询套餐关联的菜品 若有菜品的状态为停售则本能起售
        if (status.equals(StatusConstant.ENABLE)){
            //获取dish Id
            List<Long> dishIds = setmealDishMapper.getDishIdBySetmealId(id);
            //获取dish
            for (Long dishId : dishIds) {
                //查询dish中是否有没有起售的菜品
                if (dishMapper.getById(dishId).getStatus() == 0)
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        //2.停售  或者 没有未起售的菜品
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        //更新套餐
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询套餐
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> setmealList = setmealMapper.list(setmeal);
        return setmealList;
    }

    /**
     * 根据套餐id查询包含的菜品列表
     * @param setmealId
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long setmealId) {
        return setmealMapper.getDishItemBySetmealId(setmealId);
    }
}
