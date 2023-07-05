package cloud.zhfsmy.ruijidelivery.service.impl;

import cloud.zhfsmy.ruijidelivery.entity.Dish;
import cloud.zhfsmy.ruijidelivery.mapper.DishMapper;
import cloud.zhfsmy.ruijidelivery.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}
