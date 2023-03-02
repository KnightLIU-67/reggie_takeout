package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Employee;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public interface EmployeeService extends IService<Employee> {

    Boolean update(HttpServletRequest request, Employee employee);
}
