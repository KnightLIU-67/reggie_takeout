package com.itheima.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Employee;
import com.itheima.entity.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        //log.info("套餐信息：{}",setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐添加成功！");
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
        Page<SetmealDto> dtoPage = new Page<>();
        // 构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        // 添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name), Setmeal::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        // 执行查询
        setmealService.page(pageInfo, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            // 分类 id
            Long categoryId = item.getCategoryId();
            // 根据分类 id 查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                // 分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /***
     * 删除套餐ids
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功！");
    }

    /***
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        setmealService.updateStatus(status,ids);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        Long categoryId = setmealDto.getCategoryId(); // 分类 id
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
        }
        return R.success(setmealDto);
    }
    // 修改菜品，同时修改菜品对应的口味数据
    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }


    // 根据条件查询套餐集合
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        return R.success(setmealService.list(setmeal));
    }

}