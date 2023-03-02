package com.itheima.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.OrderDto;
import com.itheima.entity.Orders;
import com.itheima.service.impl.OrdersServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersServiceImpl ordersService;

    // 提交(添加)订单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    // 用户获取订单分页展示
    @GetMapping("/userPage")
    public R<Page<OrderDto>> getPage(Long page, Long pageSize) {
        return R.success(ordersService.getPage(page, pageSize));
    }

    // 后台管理端获取订单分页展示
    @GetMapping("/page")
    public R<Page<OrderDto>> page(Long page, Long pageSize, String number, String beginTime, String endTime) {
        return R.success(ordersService.getAllPage(page, pageSize, number, beginTime, endTime));
    }

    // 修改订单状态
    @PutMapping
    public R<String> update(@RequestBody Orders order) {
        ordersService.update(order);
        return R.success("修改成功");
    }
}