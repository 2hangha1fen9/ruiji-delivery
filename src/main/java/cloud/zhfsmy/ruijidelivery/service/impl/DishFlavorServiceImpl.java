package cloud.zhfsmy.ruijidelivery.service.impl;

import cloud.zhfsmy.ruijidelivery.entity.DishFlavor;
import cloud.zhfsmy.ruijidelivery.mapper.DishFlavorMapper;
import cloud.zhfsmy.ruijidelivery.service.DishFlavorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("dishFlavorService")
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
