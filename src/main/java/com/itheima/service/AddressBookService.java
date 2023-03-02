package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    // 获取当前用户的默认地址
    AddressBook getDefault();
}
