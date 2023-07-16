package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.dto.SetmealDto;
import cloud.zhfsmy.ruijidelivery.entity.Category;
import cloud.zhfsmy.ruijidelivery.entity.Setmeal;
import cloud.zhfsmy.ruijidelivery.entity.SetmealDish;
import cloud.zhfsmy.ruijidelivery.service.CategoryService;
import cloud.zhfsmy.ruijidelivery.service.SetmealDishService;
import cloud.zhfsmy.ruijidelivery.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishService setmealDishService;

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable long id) {
        LambdaQueryWrapper<Setmeal> querySetmeal = new LambdaQueryWrapper<>();
        querySetmeal.eq(Setmeal::getId, id);
        Setmeal setmeal = setmealService.getOne(querySetmeal);
        SetmealDto dto = new SetmealDto();
        //属性拷贝
        BeanUtils.copyProperties(setmeal, dto);
        //获取分类
        LambdaQueryWrapper<Category> queryCategory = new LambdaQueryWrapper<>();
        queryCategory.eq(Category::getId, setmeal.getCategoryId());
        Category category = categoryService.getOne(queryCategory);
        if (category != null) {
            dto.setCategoryName(category.getName());
        }
        //获取套餐的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishQuery = new LambdaQueryWrapper<>();
        setmealDishQuery.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(setmealDishQuery);
        dto.setSetmealDishes(list);
        return R.success(dto);
    }

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 更新套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithDish(setmealDto);

        return R.success("修改套餐成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 修改套餐状态
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.in(Setmeal::getId, ids);
        List<Setmeal> dishList = setmealService.list(query);
        dishList = dishList.stream().peek(item -> item.setStatus(status)).toList();
        if (setmealService.updateBatchById(dishList)) {
            return R.success(String.format("套餐%s成功", status == 0 ? "上架" : "下架"));
        }
        throw new BusinessException(String.format("套餐%s失败", status == 0 ? "上架" : "下架"));
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
