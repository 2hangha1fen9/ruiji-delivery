package cloud.zhfsmy.ruijidelivery.common;

/**
 * 当前请求上下文
 */
public class CurrentContext {
    private static final ThreadLocal<Long> currentThread = new ThreadLocal<>();

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return currentThread.get();
    }

    /**
     * 设置当前用户Id
     */
    public static void setCurrentUserId(Long userId) {
        currentThread.set(userId);
    }
}
