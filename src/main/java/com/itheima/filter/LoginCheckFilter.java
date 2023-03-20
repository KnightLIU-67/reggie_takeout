package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName="loginCheckFilter",urlPatterns = "/*")
@Slf4j

public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //1.获取本次请求的url
        String requestURI=request.getRequestURI();
        //log.info("拦截到请求:{}",requestURI);
        // 2.定义不需要拦截的URL地址数组
        String[] urls = new String[]{
                "/employee/login",  // 登录页面
                "/employee/logout", // 退出登录
                "/backend/**",      // 后台页面的页面的静态资源
                "/front/**"    ,     // 移动端页面的静态资源
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        // 3.判断本次请求URL是否需要拦截
        Boolean check = check(urls, requestURI);
        // 4.如果check为true则不需要处理，直接放行
        if (check) {
            log.info("登陆 id 为： {}",request.getSession().getAttribute("employee"));
            Long empId = (Long)request.getSession().getAttribute("employee");
            log.info("BaseContext.getCurrentId() 的 id 为：{}",empId);
            BaseContext.setCurrentId(empId);
            Long id = Thread.currentThread().getId();
            log.info("线程 id 为: {}", id);
            filterChain.doFilter(request, response);
            return;
        }

        // 5-1.管理员如果需要处理，则判断登录状态
        if (request.getSession().getAttribute("employee") != null) {
            // 能进入说明已经登录，直接放行
            log.info("管理员登陆 id 为： {}",request.getSession().getAttribute("employee"));
            Long empId = (Long)request.getSession().getAttribute("employee");
            //log.info("BaseContext.getCurrentId() 的 id 为：{}",empId);
            BaseContext.setCurrentId(empId);
            Long id = Thread.currentThread().getId();
            //log.info("线程 id 为: {}", id);
            filterChain.doFilter(request, response);
            return;
        }

        // 5-2.用户如果需要处理，则判断登录状态
        if (request.getSession().getAttribute("user") != null) {
            // 能进入说明已经登录，直接放行
            log.info("用户登陆 id 为： {}",request.getSession().getAttribute("user"));
            Long userId = (Long)request.getSession().getAttribute("user");
            //log.info("BaseContext.getCurrentId() 的 id 为：{}",empId);
            BaseContext.setCurrentId(userId);
            Long id = Thread.currentThread().getId();
            //log.info("线程 id 为: {}", id);
            filterChain.doFilter(request, response);
            return;
        }
        // 6.走到这里就是没登录
        // 向浏览器响应一个流，让前端读到R里面的数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 进行路径匹配检查此次请求是否要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }return false;
    }
}
