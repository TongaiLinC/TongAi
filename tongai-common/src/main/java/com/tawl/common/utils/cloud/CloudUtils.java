package com.tawl.common.utils.cloud;

import com.tawl.common.config.CloudConfig;
import com.tawl.common.core.domain.model.SmsSendBody;

import java.util.Random;

/**
 * 腾讯云工具类
 *
 * @author tongai
 */
public class CloudUtils {


    /**
     * 短信验证码发送
     *
     * @param phone 手机号
     */
    public static String sendSmsVerificationCode(String phone) {
        String code = genVerificationCode(6);
        CloudConfig.sendSmsVerificationCode(phone, code);
        return code;
    }

    /**
     * 短信发送
     *
     * @param smsSendBody 短信发送实体
     */
    public static void sendSms(SmsSendBody smsSendBody) {
        CloudConfig.sendSms(smsSendBody);
    }

    private static String genVerificationCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

}
