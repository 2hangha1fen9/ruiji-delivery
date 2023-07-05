package cloud.zhfsmy.ruijidelivery.interceptor;

import cloud.zhfsmy.ruijidelivery.common.CurrentContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();
    //白名单URL
    private String[] whiteUrls = {
            //登录页放行
            "/backend/page/login/login.html",
            "/front/page/login.html",
            "/employee/login",
            "/user/login",
            //静态资源放行
            "/**/api/**",
            "/**/images/**",
            "/**/js/**",
            "/**/plugins/**",
            "/**/styles/**",
    };

    /**
     * 登录控制拦截器
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求URL
        String requestURI = request.getRequestURI();
        //判断用户是否登录,如果登录直接放行
        Object userId = request.getSession().getAttribute("employee");
        if (request.getSession().getAttribute("employee") != null) {
            //设置当前用户ID
            CurrentContext.setCurrentUserId((Long) userId);
            return true;
        }
        //判断是否是白名单路径,如果有则放行
        for (String whiteUrl : whiteUrls) {
            if (antPathMatcher.match(whiteUrl, requestURI)) {
                return true;
            }
        }
        //判断请求是后台还是前台页面,分别重定向到登录页
        if (antPathMatcher.match("/backend/**", requestURI)) {
            response.sendRedirect("/backend/page/login/login.html");
        } else {
            response.sendRedirect("/front/page/login.html");
        }

        return true;
    }
}
