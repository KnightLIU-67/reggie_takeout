package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.BaseContext;
import com.itheima.entity.ShoppingCart;
import com.itheima.mapper.ShoppingCartMapper;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        // 获取当前登录用户的 ID
        Long userId = BaseContext.getCurrentId();
        // 给传入的购物车菜品设置用户ID
        shoppingCart.setUserId(userId);

        // 查询一下是否是首次添加
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        // 添加查询条件：根据用户ID查询
        lqw.eq(ShoppingCart::getUserId, userId);
        // 判断用户添加的是菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            // 用户添加的是菜品，添加菜品ID作为查询条件
            lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 否则，用户添加的是套餐，添加套餐ID作为查询条件
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        // 调用数据层查询购物车
        ShoppingCart shoppingCartSel = this.getOne(lqw);

        // 如果查询结果为空，则是第一次添加，把number字段设为1，插入
        if (shoppingCartSel == null) {
            shoppingCart.setNumber(1);
            // 调用数据层插入购物车数据
            this.save(shoppingCart);
            // 返回已经写入用户ID的对象
            return shoppingCart;
        }

        // 否则就不是第一次加入购物车，就直接在number字段上加1，更新
        shoppingCartSel.setNumber(shoppingCartSel.getNumber() + 1);
        // 更新
        this.updateById(shoppingCartSel);
        return shoppingCartSel;
    }

    @Override
    public List<ShoppingCart> getUserList() {
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        // 创建查询条件封装器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userId != null, ShoppingCart::getUserId, userId);
        // 按时间升序排
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        // 查询当前用户的所有购物车信息
        return this.list(lqw);
    }

    @Override
    public void clean() {
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        // 创建查询条件封装器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userId != null, ShoppingCart::getUserId, userId);
        // 按用户ID删除所有记录
        this.remove(lqw);
    }

    @Override
    public void sub(ShoppingCart shoppingCart) {
        // 1.获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        // 2.创建查询条件封装器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        // 2.1 添加查询添加：按用户ID查询
        lqw.eq(userId != null, ShoppingCart::getUserId, userId);

        // 3.判断传来的是菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            // 3.1 删除的是菜品
            lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 3.2 删除的是套餐
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // 4.查询当前购物车里该菜品/套餐的数量
        ShoppingCart shoppingCartSel = this.getOne(lqw);
        if (1 < shoppingCartSel.getNumber()) {
            // 4.1 数量大于一，直接减一
            shoppingCartSel.setNumber(shoppingCartSel.getNumber() - 1);
            // 4.2更新到数据库
            this.updateById(shoppingCartSel);
        }else{
            // 5.其他情况直接从购物车删除此商品
            this.remove(lqw);
        }


    }
}