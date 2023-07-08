package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.entity.Dish;
import cloud.zhfsmy.ruijidelivery.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    /**
     * 获取菜品列表
     */
    @GetMapping("/page")
    public R<Page<Dish>> page(int page, int pageSize) {
        //获取结果
        Page<Dish> pageResult = dishService.page(new Page<>(page, pageSize));
        if (pageResult == null) {
            throw new BusinessException("数据获取失败");
        }
        return R.success(pageResult);
    }

    /**
     * 根据菜品ID获取菜品信息
     */
    @GetMapping("/{id}")
    public R<Dish> getDishById(@PathVariable Long id) {
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.eq(Dish::getId, id);
        Dish dish = dishService.getOne(query);
        return R.success(dish);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> addDish(@RequestBody Dish dish) {
        checkDish(dish);

        boolean save = dishService.save(dish);
        if (!save) {
            throw new BusinessException("菜品添加失败");
        }
        return R.success("菜品添加失败");
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> editDish(@RequestBody Dish dish) {
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.eq(Dish::getId, dish.getId());
        Dish exDish = dishService.getOne(query);
        if (exDish == null) {
            throw new BusinessException("菜品不存在");
        }
        boolean save = dishService.updateById(dish);
        if (!save) {
            throw new BusinessException("菜品修改失败");
        }
        return R.success("菜品修改成功");
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam Long ids) {
        LambdaQueryWrapper<Dish> dishQuery = new LambdaQueryWrapper<>();
        dishQuery.eq(Dish::getId, ids);
        boolean remove = dishService.remove(dishQuery);
        if (!remove) {
            throw new BusinessException("菜品删除失败");
        }
        return R.success("菜品删除成功");
    }

    /**
     * 模型验证
     */
    private void checkDish(Dish dish) {
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
            LambdaQueryWrapper<Dish> exQuery = new LambdaQueryWrapper<>();
            exQuery.eq(Dish::getName, dish.getName());
            Dish exDish = dishService.getOne(exQuery);
            if (exDish != null) {
                throw new BusinessException("菜品已存在");
            }
        }
    }
}
