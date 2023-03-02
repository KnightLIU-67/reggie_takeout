package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //购物车商品增加
    ShoppingCart add(ShoppingCart shoppingCart);

    // 查询当前用户的购物车中所有信息
    List<ShoppingCart> getUserList();

    //清空购物车
    void clean();

    //购物车商品减一
    void sub(ShoppingCart shoppingCart);
}