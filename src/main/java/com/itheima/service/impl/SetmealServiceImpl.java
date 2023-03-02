package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.mapper.CategoryMapper;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作 setmeal, 执行 insert 操作：
        this.save(setmealDto);//setmealDto里面有setmeal，所以可以直接save
        //插入到SetmealDish表中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }
    /***
     * 删除套餐，也要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional //事务
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态, 确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count1 = this.count(queryWrapper);
        if (count1 > 0) {
            // 已经关联菜品，抛出一个业务异常
            throw new CustomException("套餐正在售卖菜品，不能删除！");
        }
        //ok,删除本表中的id
        this.removeByIds(ids);
        //删除setmealDish里面的id
        //delete from setmeal_ dish where setmeal_id in1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /***
     * (批量)套餐启售/停售
     * @param status
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public void updateStatus(Integer status, List<Long> ids) {
        // 启售套餐前，应检查套餐关联菜品是否存在且在售
        if (status == 1) {
            // 根据套餐IDs从setmeal_dish表查询对应的菜品IDs
            LambdaQueryWrapper<SetmealDish> lqw1 = new LambdaQueryWrapper<>();
            // 添加过滤条件：查询停售的dish
            lqw1.eq(SetmealDish::getId, ids);
            // 根据条件封装器lqw查询满足条件的dish实体类对象构成的集合
            List<SetmealDish> setmealDishes = setmealDishService.list(lqw1);
            Set<Long> dishIds  = setmealDishes.stream().map(SetmealDish::getDishId).collect(Collectors.toSet());

            // 创建dish的条件包装器
            LambdaQueryWrapper<Dish> lqw2 = new LambdaQueryWrapper<>();
            // 添加过滤条件：查询停售的dish
            lqw2.eq(Dish::getStatus, 0);
            // 根据条件封装器lqw查询满足条件的dish实体类对象构成的集合
            List<Dish> dishes = dishService.list(lqw2);
            // 如果dish集合不为空，说明套餐菜品中有停售菜品，套餐不能启售，抛出业务异常
            if (!dishes.isEmpty()) {
                // 获取已停售菜品的名称
                List<String> dishNames = dishes.stream().map(Dish::getName).collect(Collectors.toList());
                // 向前端页面展示已停售菜品的名称，方便用户启售对应菜品
                throw new CustomException("套餐所关联菜品" + dishNames + "已停售，启售失败");
            }
            if (!dishIds.isEmpty()){
                // 检查菜品有没有被删除
                LambdaQueryWrapper<Dish> lqw3 = new LambdaQueryWrapper<>();
                lqw3.in(Dish::getId, dishIds);
                List<Dish> dishes1 = dishService.list(lqw3);
                // 如果套餐菜品个数与dish数据表中根据菜品ID查询回来的个数不相等，说明dish数据表中有菜品被删除了
                if (dishes1.size() != dishIds.size()) {
                    throw new CustomException("套餐中有菜品被删除，启售失败");
                }
            }
        }
        // 1.根据套餐ID集合批量查询套餐
        List<Setmeal> setmeals = this.listByIds(ids);
        // 2.使用stream流逐一修改其售卖状态
        setmeals = setmeals.stream().peek(setmeal -> setmeal.setStatus(status)).collect(Collectors.toList());
        // 3.批量修改
        this.updateBatchById(setmeals);
    }
    /***
     * 根据id来查询信息和口味
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        // 查询菜品基本信息，从 dish 表查询
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询当前菜品对应的口味信息，从 dish_flavor 表查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        // 更新 setmeal 表的基本信息
        this.updateById(setmealDto);
        // 清理当前套餐对应的菜品数据 --- setmeal_dish 的 delete 操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 添加当前提交过来的菜品数据 --- setmeal_dish 的 insert 操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    // 根据条件查询套餐集合
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        // 1.创建查询条件封装器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        // 2.添加查询条件：根据类别ID查询
        lqw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        // 3.添加查询条件：根据售卖状态查询
        lqw.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        // 4.调用数据层返回套餐对象构成的集合
        return this.list(lqw);
    }
}
