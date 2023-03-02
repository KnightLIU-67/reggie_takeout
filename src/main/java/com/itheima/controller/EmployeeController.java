package com.itheima.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
//@RestController注解，相当于@Controller+@ResponseBody两个注解的结合，返回json数据不需要在方法前面加@ResponseBody注解了，但使用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面
@Slf4j//开启日志
@RequestMapping("/employee")
public class EmployeeController {
    //这里自动装配不推荐使用字段注入，因此手动写初始方法
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        employee.setPassword(password);
        System.out.println(employee.toString());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登陆失败结果
        if (emp==null){
            return R.error("登录失败，用户不存在");
        }

        //4.密码比对
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败，密码错误");
        }

        //5.查看员工状态，如果是禁用状态则返回，员工已被禁用
        if (emp.getStatus()==0){
            return R.error("账号已被禁用");
        }

        //6.登陆成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    /*
    员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        //1.清理Session中保存的当前登陆员工的id
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }
    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        // 1.设置初始密码为123456，并经过MD5加密
        String psw = "123456";
        String password = DigestUtils.md5DigestAsHex(psw.getBytes());
        employee.setPassword(password);

        // 2.调用业务层保存到数据库中
        employeeService.save(employee);
        return R.success("添加成功");
    }
    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }
    /*
    @param
    根据id修改员工信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        if (employeeService.update(request,employee)){
            return R.success("员工信息修改成功！！");
        }
        return R.error("员工信息修改出错~~");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        // 当查询结果不为空时才返回employee
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("查询员工不存在");
    }



}
