package cloud.zhfsmy.ruijidelivery.service.impl;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.dto.DishDto;
import cloud.zhfsmy.ruijidelivery.entity.Category;
import cloud.zhfsmy.ruijidelivery.entity.Dish;
import cloud.zhfsmy.ruijidelivery.entity.DishFlavor;
import cloud.zhfsmy.ruijidelivery.mapper.DishMapper;
import cloud.zhfsmy.ruijidelivery.service.CategoryService;
import cloud.zhfsmy.ruijidelivery.service.DishFlavorService;
import cloud.zhfsmy.ruijidelivery.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;

    @Override
    public Page<DishDto> page(int page, int pageSize, String name) {
        //查询菜品
        LambdaQueryWrapper<Dish> dishQuery = new LambdaQueryWrapper<>();
        dishQuery.orderByDesc(Dish::getUpdateTime);
        dishQuery.like(name != null, Dish::getName, name);
        Page<Dish> dishPage = this.page(new Page<>(page, pageSize), dishQuery);
        //查询所有分类
        LambdaQueryWrapper<Category> categoryQuery = new LambdaQueryWrapper<>();
        categoryQuery.in(
                dishPage.getRecords().size() > 0,
                Category::getId,
                dishPage.getRecords()
                        .stream()
                        .map(Dish::getCategoryId)
                        .collect(Collectors.toList()));
        List<Category> categoryList = categoryService.list(categoryQuery);
        //构造返回对象
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        //设置对应分类
        List<DishDto> collect = dishPage.getRecords().stream().map(item -> {
            //获取当前条对应分类
            Optional<Category> category = categoryList.stream().filter(c -> Objects.equals(c.getId(), item.getCategoryId())).findFirst();
            //映射为dto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //设置对应分类
            category.ifPresent(c -> {
                dishDto.setCategoryName(c.getName());
            });
            return dishDto;
        }).toList();
        dishDtoPage.setRecords(collect);
        return dishDtoPage;
    }

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基础信息
        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品id
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //给每个口味加上菜品ID
        flavors = flavors.stream().peek(item -> item.setDishId(dishId)).toList();
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置对应菜品ID
        flavors = flavors.stream().peek((item) -> item.setDishId(dishDto.getId())).toList();

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeBatch(List<Long> ids) {
        //批量删除菜品
        boolean dish = this.removeBatchByIds(ids);
        //删除对应菜品味道
        LambdaQueryWrapper<DishFlavor> dishFlavorQuery = new LambdaQueryWrapper<>();
        dishFlavorQuery.in(DishFlavor::getDishId, ids);
        boolean flavor = dishFlavorService.remove(dishFlavorQuery);
        if (!(dish && flavor)) {
            throw new BusinessException("删除菜品失败");
        }
    }
}
