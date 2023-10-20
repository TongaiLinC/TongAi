package com.tawl.common.utils.cloud;

import com.tawl.common.config.CloudConfig;
import com.tawl.common.core.domain.model.SmsSendBody;
import com.tawl.common.core.domain.model.UpLoadFile;

import java.io.InputStream;
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

    /**
     * 上传文件
     *
     * @param fileInputStream 文件流
     * @param filePath        文件路径(文件存储位置例：filePath/a.jpg)
     * @return 文件上传地址
     */
    public static String uploadFile(InputStream fileInputStream, String filePath) {
        return CloudConfig.upload(fileInputStream, filePath);
    }

    /**
     * 上传文件(自定义)
     *
     * @param upLoadFile 上传文件对象
     * @return 文件上传地址
     */
    public static String uploadFile(UpLoadFile upLoadFile) {
        return CloudConfig.upload(upLoadFile.getFileInputStream(), upLoadFile.getFilePath());
    }

    /**
     * 获取随机数
     *
     * @param length 随机数长度
     * @return 随机数
     */
    private static String genVerificationCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

}
