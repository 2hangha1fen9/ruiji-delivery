package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.dto.DishDto;
import cloud.zhfsmy.ruijidelivery.entity.Dish;
import cloud.zhfsmy.ruijidelivery.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    /**
     * 获取菜品列表
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<DishDto> result = dishService.page(page, pageSize, name);
        return R.success(result);
    }

    /**
     * 根据菜品ID获取菜品信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id) {
        DishDto dish = dishService.getByIdWithFlavor(id);
        return R.success(dish);
    }

    @GetMapping("/list")
    public R<List<Dish>> getDishListByCategoryId(@RequestParam Long categoryId) {
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.eq(Dish::getCategoryId, categoryId);
        List<Dish> list = dishService.list(query);
        return R.success(list);
    }

    /**
     * 修改菜品状态
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.in(Dish::getId, ids);
        List<Dish> dishList = dishService.list(query);
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
    public R<String> addDish(@RequestBody DishDto dish) {
        checkDish(dish);
        dishService.saveWithFlavor(dish);
        return R.success("菜品添加成功");
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> editDish(@RequestBody DishDto dish) {
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.eq(Dish::getId, dish.getId());
        Dish exDish = dishService.getOne(query);
        if (exDish == null) {
            throw new BusinessException("菜品不存在");
        }
        dishService.updateWithFlavor(dish);
        return R.success("菜品修改成功");
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids) {
        dishService.removeBatch(ids);
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
