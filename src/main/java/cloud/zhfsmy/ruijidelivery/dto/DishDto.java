package cloud.zhfsmy.ruijidelivery.dto;

import cloud.zhfsmy.ruijidelivery.entity.Dish;
import cloud.zhfsmy.ruijidelivery.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
