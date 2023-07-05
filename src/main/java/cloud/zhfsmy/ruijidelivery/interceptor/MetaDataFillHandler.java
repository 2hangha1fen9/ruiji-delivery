package cloud.zhfsmy.ruijidelivery.interceptor;

import cloud.zhfsmy.ruijidelivery.common.CurrentContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充字段
 */
@Component
public class MetaDataFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", CurrentContext.getCurrentUserId());
        metaObject.setValue("updateUser", CurrentContext.getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", CurrentContext.getCurrentUserId());
    }
}
