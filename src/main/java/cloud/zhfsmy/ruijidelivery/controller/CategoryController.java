package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.entity.Category;
import cloud.zhfsmy.ruijidelivery.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类列表
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        //获取结果
        Page<Category> pageResult = categoryService.page(new Page<>(page, pageSize));
        if (pageResult == null) {
            return R.error("数据获取失败");
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
     * 新增员工
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        String checkResult = checkCategory(category);
        if (!checkResult.isEmpty()) {
            return R.error(checkResult);
        }
        boolean save = categoryService.save(category);
        if (save) {
            return R.success("分类添加成功");
        }
        return R.success("分类添加失败");
    }

    /**
     * 修改员工
     */
    @PutMapping
    public R<String> editCategory(@RequestBody Category category) {
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
        query.eq(Category::getId, category.getId());
        Category exCategory = categoryService.getOne(query);
        if (exCategory == null) {
            return R.error("分类不存在");
        }
        boolean save = categoryService.updateById(category);
        if (save) {
            return R.success("分类修改成功");
        }
        return R.success("分类修改失败");
    }

    /**
     * 模型验证
     */
    private String checkCategory(Category category) {
        if (category.getName() == null || category.getName().length() == 0) {
            return "分类名称不能为空";
        }
        if (category.getSort() == null) {
            return "排序不能为空";
        }
        //修改时不验证
        if (category.getId() == null) {
            LambdaQueryWrapper<Category> exQuery = new LambdaQueryWrapper<>();
            exQuery.eq(Category::getName, category.getName());
            Category exCategory = categoryService.getOne(exQuery);
            if (exCategory != null) {
                return "分类已存在";
            }
        }

        return "";
    }
}
