package com.sky.controller.admin;

import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类类管理
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(value = "分类管理相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


}
