package cloud.zhfsmy.ruijidelivery.service.impl;

import cloud.zhfsmy.ruijidelivery.entity.Category;
import cloud.zhfsmy.ruijidelivery.mapper.CategoryMapper;
import cloud.zhfsmy.ruijidelivery.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
