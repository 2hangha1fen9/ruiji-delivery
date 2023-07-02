package cloud.zhfsmy.ruijidelivery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Slf4j
@Configuration
public class WebMVCConfig extends WebMvcConfigurationSupport {
    /**
     * 配置静态资源映射
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**", "/front/**")
                .addResourceLocations("classpath:/backend/", "classpath:/front/");
        registry.addResourceHandler("/backend")
                .addResourceLocations("classpath:/backend/index.html");
    }
}
