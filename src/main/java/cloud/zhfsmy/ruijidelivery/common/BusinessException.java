package cloud.zhfsmy.ruijidelivery.common;

/**
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
