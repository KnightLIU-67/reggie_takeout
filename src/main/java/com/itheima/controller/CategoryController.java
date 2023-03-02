package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
//@RestController注解，相当于@Controller+@ResponseBody两个注解的结合，返回json数据不需要在方法前面加@ResponseBody注解了，但使用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面
@Slf4j//开启日志
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category) {

        log.info("新增category信息:{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件, 根据 sort 进行排序
        queryWrapper.orderByAsc(Category::getSort);
        // 进行分页查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据 id 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("删除分类: id 为:{}", id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }
    /**
     * 根据 id 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类信息：{}", category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
    // 添加菜品或套餐时获取分类信息
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //查询必备
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType() != null, Category::getType, category.getType());
        // 双重排序条件
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return R.success(categoryService.list(lqw));
    }

}
