package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.common.ValidateCodeUtils;
import cloud.zhfsmy.ruijidelivery.entity.User;
import cloud.zhfsmy.ruijidelivery.service.UserService;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private com.aliyun.dysmsapi20170525.Client smsClient;

    @Value("${sms.sign-name}")
    private String singName;
    @Value("${sms.template.valid-code}")
    private String templateCode;

    /**
     * 发送短信
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        //获取手机号
        String phone = user.getPhone();
        if ((phone = phone.trim()).isEmpty()) {
            throw new BusinessException("手机号不能为空");
        }
        //生成验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        //保存到session中
        request.getSession().setAttribute(phone, code);
        //调用阿里云发送短信
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(singName)
                .setTemplateCode(templateCode)
                .setTemplateParam(String.format("{\"code\":\"%s\"}", code));
        // 复制代码运行请自行打印 API 的返回值
        try {
            SendSmsResponse sendSmsResponse = smsClient.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
            log.info(sendSmsResponse.getBody().message);
            return R.success(sendSmsResponse.getBody().message);
        } catch (Exception e) {
            throw new BusinessException("短信发送失败");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpServletRequest request) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //获取session中验证码
        String savedCode = request.getSession().getAttribute(phone).toString();
        if (!code.equals(savedCode)) {
            throw new BusinessException("验证码错误");
        }
        //查询用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getPhone, phone);
        User user = userService.getOne(query);
        //如果用户存在则登录,不存在则直接注册
        if (user != null) {
            //登录成功保存用户session
            request.getSession().setAttribute("user", user.getId());
            return R.success("登录成功");
        } else {
            user = new User();
            user.setPhone(phone);
            user.setName(phone);
            user.setStatus(1);
            boolean save = userService.save(user);
            if (save) {
                return R.success("注册成功");
            }
            return R.error("注册失败");
        }
    }
}
