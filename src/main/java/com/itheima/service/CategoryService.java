package com.itheima.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;

public interface CategoryService extends IService<Category> {
    void remove(Long id);

}
