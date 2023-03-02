package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.entity.Employee;
import com.itheima.mapper.CategoryMapper;
import com.itheima.mapper.DishMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 1.保存菜品的基本信息
        log.info("dishDto:{}",dishDto.toString());
        Long categoryId = Long.valueOf(dishDto.getCategoryName());
        dishDto.setCategoryId(categoryId);
        this.save(dishDto);
        // 2.获取菜品的ID
        Long dishId = dishDto.getId();
        // 3.遍历菜品口味并逐一赋上菜品ID值，然后保存
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 3.1 使用Stream处理集合，结果赋回给它自己
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味数据到菜品口味表 dish_flavor
        log.info("saveWithFlavor:{}",flavors);
        dishFlavorService.saveBatch(flavors);
    }

    /***
     * 根据id来查询信息和口味
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从 dish 表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        // 查询当前菜品对应的口味信息，从 dish_flavor 表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        log.info(dishDto.toString());
        // 更新 dish 表的基本信息
        this.updateById(dishDto);
        // 清理当前菜品对应的口味数据 --- dish_flavor 的 delete 操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加当前提交过来的口味数据 --- dish_flavor 的 insert 操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void removeWithDishFlavor(List<Long> ids) {
        // 查询套餐状态, 确定是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count1 = this.count(queryWrapper);
        if (count1 > 0) {
            // 已经关联菜品，抛出一个业务异常
            throw new CustomException("正在售卖菜品，不能删除！");
        }
        //ok,删除本表中的id
        this.removeByIds(ids);
        //删除 DishFlavor 里面的id
        //delete from setmeal_ dish where setmeal_id in1,2,3)
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        // 1.根据菜品ID集合批量查询菜品
        List<Dish> dishes = this.listByIds(ids);
        // 2.使用stream流逐一修改其售卖状态
        dishes = dishes.stream().peek(dish -> dish.setStatus(status)).collect(Collectors.toList());
        // 3.批量修改
        this.updateBatchById(dishes);
    }


}
