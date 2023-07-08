package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.dto.SetmealDto;
import cloud.zhfsmy.ruijidelivery.entity.Setmeal;
import cloud.zhfsmy.ruijidelivery.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService dishService;

    /**
     * 获取菜品列表
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDto> result = dishService.page(page, pageSize, name);
        return R.success(result);
    }

    /**
     * 根据菜品ID获取菜品信息
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id) {
        SetmealDto dish = dishService.getByIdWithFlavor(id);
        return R.success(dish);
    }

    /**
     * 修改菜品状态
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.in(Setmeal::getId, ids);
        List<Setmeal> dishList = dishService.list(query);
        dishList = dishList.stream().peek(item -> item.setStatus(status)).toList();
        if (dishService.updateBatchById(dishList)) {
            return R.success(String.format("菜品%s成功", status == 0 ? "上架" : "下架"));
        }
        throw new BusinessException(String.format("菜品%s失败", status == 0 ? "上架" : "下架"));
    }

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto dish) {
        checkSetmeal(dish);
        dishService.saveWithFlavor(dish);
        return R.success("菜品添加成功");
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> editSetmeal(@RequestBody SetmealDto dish) {
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.eq(Setmeal::getId, dish.getId());
        Setmeal exSetmeal = dishService.getOne(query);
        if (exSetmeal == null) {
            throw new BusinessException("菜品不存在");
        }
        dishService.updateWithFlavor(dish);
        return R.success("菜品修改成功");
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids) {
        dishService.removeBatch(ids);
        return R.success("菜品删除成功");
    }

    /**
     * 模型验证
     */
    private void checkSetmeal(Setmeal dish) {
        if (dish.getName() == null || dish.getName().length() == 0) {
            throw new BusinessException("菜品名称不能为空");
        }
        if (dish.getPrice() == null) {
            throw new BusinessException("菜品价格不能为空");
        }
        if (dish.getImage() == null || dish.getImage().length() == 0) {
            throw new BusinessException("菜品图片不能为空");
        }
        //修改时不验证
        if (dish.getId() == null) {
            LambdaQueryWrapper<Setmeal> exQuery = new LambdaQueryWrapper<>();
            exQuery.eq(Setmeal::getName, dish.getName());
            Setmeal exSetmeal = dishService.getOne(exQuery);
            if (exSetmeal != null) {
                throw new BusinessException("菜品已存在");
            }
        }
    }
}
