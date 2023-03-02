package com.itheima.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.CustomException;
import com.itheima.common.R;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session) {
        //获取手机号

        String phone = user.getPhone();

        if (!StringUtils.isEmpty(phone)) {
            String code = genCode();
            log.info("生成的验证码为：{}", code);
            session.setAttribute("phone",phone);
            session.setAttribute("code",code);
            //log.info("session中 有 {} and {}", session.getAttribute("phone"),session.getAttribute("code"));

            return R.success("验证码发送成功！");
        }
        return R.error("验证码发送失败~~");
    }

    /***
     * user和code一起传进来
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //log.info("session中 有 {} and {}", session.getAttribute("phone"),session.getAttribute("code"));
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        String trueCode = (String) session.getAttribute("code");
        // 验证邮箱和验证码是否为空，如果为空则直接登录失败
        if (phone.isEmpty() || code.isEmpty()) {
            throw new CustomException("邮箱或验证码不能为空");
        }
        // 比对用户输入的验证码和真实验证码，错了直接登录失败
        if (!code.equals(trueCode)) {
            return R.error("验证码错误，登录失败~~");
        }
        // 验证码匹配，开始调用数据库查询
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        User user = userService.getOne(lqw);
        if (user == null) {
            // 判断当前手机号对应的用户是否是新用户，如果是新用户就自动完成注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());
        return R.success(user);
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        //1.清理Session中保存的当前登陆用户的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


    public String genCode () {
        String datas = "0123456789";
        Random r = new Random();
        String code = "";
        int n = 4;
        for (int i = 0; i < n; i++) {
            int index = r.nextInt(datas.length());
            char data = datas.charAt(index);
            code += data;
        }
        return code;
    }
}






