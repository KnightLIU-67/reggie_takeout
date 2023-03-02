package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.BaseContext;
import com.itheima.entity.AddressBook;
import com.itheima.mapper.AddressBookMapper;
import com.itheima.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Override
    public AddressBook getDefault() {
        // 1.获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        // 2.创建查询条件封装器
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        // 3.添加查询条件：根据用户ID进行查询
        lqw.eq(userId != null, AddressBook::getUserId, userId);
        // 4.添加查询条件：查询是默认地址的地址
        lqw.eq(AddressBook::getIsDefault, 1);
        // 5.调用数据层根据查询条件封装器查询
        return this.getOne(lqw);
    }
}
