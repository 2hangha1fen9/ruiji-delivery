package cloud.zhfsmy.ruijidelivery.service;

import cloud.zhfsmy.ruijidelivery.dto.SetmealDto;
import cloud.zhfsmy.ruijidelivery.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 修改套餐
     *
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     *
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
