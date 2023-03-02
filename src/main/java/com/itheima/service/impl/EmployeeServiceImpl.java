package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Employee;
import com.itheima.mapper.EmployeeMapper;
import com.itheima.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    // 修改员工信息
    @Override
    public Boolean update(HttpServletRequest request, Employee employee) {
        return updateById(employee);
    }


}

