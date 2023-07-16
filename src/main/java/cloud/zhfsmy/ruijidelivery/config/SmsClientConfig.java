package cloud.zhfsmy.ruijidelivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsClientConfig {
    @Value("${sms.access-key-id}")
    private String accessKeyId;
    @Value("${sms.access-key-secret}")
    private String accesskeySecret;

    @Bean
    public com.aliyun.dysmsapi20170525.Client smsClient() {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accesskeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        try {
            return new com.aliyun.dysmsapi20170525.Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
