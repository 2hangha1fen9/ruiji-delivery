package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.entity.Category;
import cloud.zhfsmy.ruijidelivery.entity.Dish;
import cloud.zhfsmy.ruijidelivery.entity.Setmeal;
import cloud.zhfsmy.ruijidelivery.service.CategoryService;
import cloud.zhfsmy.ruijidelivery.service.DishService;
import cloud.zhfsmy.ruijidelivery.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;

    /**
     * 获取分类列表
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        //获取结果
        Page<Category> pageResult = categoryService.page(new Page<>(page, pageSize));
        if (pageResult == null) {
            throw new BusinessException("数据获取失败");
        }
        return R.success(pageResult);
    }

    /**
     * 根据分类ID获取分类信息
     */
    @GetMapping("/{id}")
    public R<Category> getCategoryById(@PathVariable Long id) {
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
        query.eq(Category::getId, id);
        Category category = categoryService.getOne(query);
        return R.success(category);
    }

    /**
     * 新增分类
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        checkCategory(category);

        boolean save = categoryService.save(category);
        if (!save) {
            throw new BusinessException("分类添加失败");
        }
        return R.success("分类添加失败");
    }

    /**
     * 修改分类
     */
    @PutMapping
    public R<String> editCategory(@RequestBody Category category) {
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
        query.eq(Category::getId, category.getId());
        Category exCategory = categoryService.getOne(query);
        if (exCategory == null) {
            throw new BusinessException("分类不存在");
        }
        boolean save = categoryService.updateById(category);
        if (!save) {
            throw new BusinessException("分类修改失败");
        }
        return R.success("分类修改成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> deleteCategory(@RequestParam Long ids) {
        //查询关联菜品
        LambdaQueryWrapper<Dish> dishQuery = new LambdaQueryWrapper<>();
        dishQuery.eq(Dish::getCategoryId, ids);
        long dishCount = dishService.count(dishQuery);
        if (dishCount > 0) {
            throw new BusinessException("该分类下还有关联菜品,不能删除");
        }
        //查询关联套餐
        LambdaQueryWrapper<Setmeal> setQuery = new LambdaQueryWrapper<>();
        setQuery.eq(Setmeal::getCategoryId, ids);
        long setCount = setmealService.count(setQuery);
        if (setCount > 0) {
            throw new BusinessException("该分类下还有关联套餐,不能删除");
        }
        LambdaQueryWrapper<Category> categoryQuery = new LambdaQueryWrapper<>();
        categoryQuery.eq(Category::getId, ids);
        boolean remove = categoryService.remove(categoryQuery);
        if (!remove) {
            throw new BusinessException("分类删除失败");
        }
        return R.success("分类删除成功");
    }

    /**
     * 模型验证
     */
    private void checkCategory(Category category) {
        if (category.getName() == null || category.getName().length() == 0) {
            throw new BusinessException("分类名称不能为空");
        }
        if (category.getSort() == null) {
            throw new BusinessException("排序不能为空");
        }
        //修改时不验证
        if (category.getId() == null) {
            LambdaQueryWrapper<Category> exQuery = new LambdaQueryWrapper<>();
            exQuery.eq(Category::getName, category.getName());
            Category exCategory = categoryService.getOne(exQuery);
            if (exCategory != null) {
                throw new BusinessException("分类已存在");
            }
        }
    }
}
