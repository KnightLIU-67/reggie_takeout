package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /***
     * 由于要操作两张表，在 SetmealService 中扩展 saveWithDish ：
     * 新增套餐，同时保存套餐和菜品的关联关系
     */
    public void saveWithDish(SetmealDto setmealDto);

    /***
     * 删除套餐，也要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /***
     * (批量)套餐启售/停售
     * @param status
     * @param ids
     * @return
     */
    public void updateStatus(Integer status, List<Long> ids);
    /***
     * 根据id来查询菜品和分类
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);

    // 根据条件查询套餐集合
    List<Setmeal> list(Setmeal setmeal);

}
