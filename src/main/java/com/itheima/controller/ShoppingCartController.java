package com.itheima.controller;

import com.itheima.common.R;
import com.itheima.entity.ShoppingCart;
import com.itheima.service.impl.ShoppingCartServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    // 添加菜品到购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        return R.success(shoppingCartService.add(shoppingCart));
    }

    // 查询当前用户的购物车中所有信息
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        return R.success(shoppingCartService.getUserList());
    }

    // 清空购物车
    @DeleteMapping("/clean")
    public R<String> clean() {
        shoppingCartService.clean();
        return R.success("购物车清空成功");
    }

    // 购物车商品减一
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.sub(shoppingCart);
        return R.success("删除成功");
    }
}