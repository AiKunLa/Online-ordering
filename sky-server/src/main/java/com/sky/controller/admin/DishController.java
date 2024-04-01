package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品:{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result saveWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);

        cleanCache("dish_" + dishDTO.getCategoryId());

        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品起售、停售
     *
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "菜品起售、停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品起售、停售:{}", status);

        cleanCache("dish_*");

        dishService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("菜品起售、停售:{}", id);
        DishVO dishVO = dishService.queryById(id);
        return Result.success(dishVO);
    }

//    /**
//     * 根据分类id查询菜品
//     * @param categoryId
//     * @return
//     */
//    @GetMapping("/list")
//    @ApiOperation(value = "根据分类id查询菜品")
//    public Result<PageResult> list(Integer categoryId){
//        log.info("根据分类id查询菜品:{}",categoryId);
//        PageResult pageResult = dishService.list(categoryId);
//        return Result.success(pageResult);
//    }

    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询菜品:{}", categoryId);
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }

    /**
     * 批量删除菜品
     *
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) { // 前端传过来的是 11,2,3,6 这样的字符串 添加@RequestParam 可以自动转换类型
        log.info("批量删除菜品: {}");

        //清除所有菜品缓存
        cleanCache("dish_*");

        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 清除缓存中的数据
     * @param pattern
     */
    private void cleanCache(String pattern) {
        //获取所有key
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
