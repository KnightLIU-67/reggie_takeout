package com.itheima.dto;

import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    // 封装菜品口味
    private List<SetmealDish> setmealDishes = new ArrayList<>();

    // 菜品分类名称
    private String categoryName;

}