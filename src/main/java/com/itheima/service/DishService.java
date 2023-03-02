package com.itheima.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，不仅要存入dish，还有存入dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id来查询信息和口味
    public DishDto getByIdWithFlavor(Long id);
    //修改菜品，不仅要存入dish，还有存入dish_flavor
    public void updateWithFlavor(DishDto dishDto);
    //删除菜品
    public void removeWithDishFlavor(List<Long> ids);
    //(批量)菜品启售/停售
    public void updateStatus(Integer status, List<Long> ids);
}



