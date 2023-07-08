package cloud.zhfsmy.ruijidelivery.dto;

import cloud.zhfsmy.ruijidelivery.entity.Setmeal;
import cloud.zhfsmy.ruijidelivery.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
