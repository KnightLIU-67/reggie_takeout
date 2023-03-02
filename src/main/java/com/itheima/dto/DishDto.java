package com.itheima.dto;

import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    // 封装菜品口味
    private List<DishFlavor> flavors = new ArrayList<>();

    // 菜品分类名称
    private String categoryName;


    // 副本
    private Integer copies;
}