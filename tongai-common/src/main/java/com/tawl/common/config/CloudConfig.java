package com.tawl.common.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.google.gson.Gson;
import com.tawl.common.constant.CloudConstants;
import com.tawl.common.core.domain.model.SmsSendBody;
import com.tawl.common.exception.UtilException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 腾讯云配置
 *
 * @author tongai
 */
@Configuration
public class CloudConfig {
    /**
     * 腾讯云API调用秘钥ID
     */
    private static String secretId;

    /**
     * 腾讯云API调用秘钥Key
     */
    private static String secretKey;

    /**
     * 地域信息
     */
    private static String region;

    /**
     * 云服务平台信息
     * 腾讯云：tx  阿里云: al  七牛云：qn
     */
    private static String platform;

    /**
     * 应用ID
     */
    private static String sdkAppId;

    /**
     * 短信验证码模板ID
     */
    private static String templateId;

    /**
     * 短信验证码签名名称
     */
    private static String signName;

    /**
     * 短信验证码过期时间(分钟)
     */
    private static String expireTime;

    @Value("${cloud.secretId}")
    public void setSecretId(String secretId) {
        CloudConfig.secretId = secretId;
    }

    @Value("${cloud.secretKey}")
    public void setSecretKey(String secretKey) {
        CloudConfig.secretKey = secretKey;
    }

    @Value("${cloud.sms.region}")
    public void setRegion(String region) {
        CloudConfig.region = region;
    }

    @Value("${cloud.platform}")
    public void setPlatform(String platform) {
        CloudConfig.platform = platform;
    }

    @Value("${cloud.sms.sdkAppId}")
    public void setSdkAppId(String sdkAppId) {
        CloudConfig.sdkAppId = sdkAppId;
    }

    @Value("${cloud.sms.templateId}")
    public void setTemplateId(String templateId) {
        CloudConfig.templateId = templateId;
    }

    @Value("${cloud.sms.signName}")
    public void setSignName(String signName) {
        CloudConfig.signName = signName;
    }

    @Value("${cloud.sms.expireTime}")
    public void setExpireTime(String expireTime) {
        CloudConfig.expireTime = expireTime;
    }

    /**
     * 发送短信(自定义)
     *
     * @param smsSendBody 短信发送请求体
     */
    public static void sendSms(SmsSendBody smsSendBody) {
        if (CloudConstants.TXCLOUD.equals(platform)) {
            sendTxSms(smsSendBody);
            return;
        }
        if (CloudConstants.ALIYUN.equals(platform)) {
            sendAliSms(smsSendBody);
        }
    }

    /**
     * 发送短信客户端(腾讯)
     *
     * @param smsSendBody 短信发送请求体
     */
    private static void sendTxSms(SmsSendBody smsSendBody) {
        Credential cred = new Credential(secretId, secretKey);
        SmsClient smsClient = new SmsClient(cred, region);
        com.tencentcloudapi.sms.v20210111.models.SendSmsRequest req = new com.tencentcloudapi.sms.v20210111.models.SendSmsRequest();
        req.setSmsSdkAppId(smsSendBody.getSdkAppId());
        req.setSignName(smsSendBody.getSignName());
        req.setTemplateId(smsSendBody.getTemplateId());
        req.setTemplateParamSet(smsSendBody.getParams());
        req.setPhoneNumberSet(smsSendBody.getPhoneNumbers());
        try {
            SendSmsResponse response = smsClient.SendSms(req);
            System.out.println("======================短信发送结果：" + response.toString());
        } catch (TencentCloudSDKException e) {
            throw new UtilException("短信发送异常：" + e.getMessage());
        }
    }

    /**
     * 发送短信客户端(阿里异步)
     *
     * @param smsSendBody 短信发送请求体
     */
    private static void sendAliSms(SmsSendBody smsSendBody) {
        StaticCredentialProvider provider = StaticCredentialProvider.create(com.aliyun.auth.credentials.Credential.builder()
                .accessKeyId(secretId)
                .accessKeySecret(secretKey)
                .build());
        AsyncClient asyncClient = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration
                                .create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com"))
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(smsSendBody.getPhoneNumber())
                .signName(smsSendBody.getSignName())
                .templateCode(smsSendBody.getTemplateId())
                .templateParam(smsSendBody.getParam())
                .build();

        CompletableFuture<com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse> response = asyncClient.sendSms(sendSmsRequest);
        com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new UtilException("阿里云短信发送错误：" + e.getMessage());
        }
        String json = new Gson().toJson(resp);
        asyncClient.close();
        JSONObject jsonObject = JSON.parseObject(json);
        String status = jsonObject.getString("code");
        System.out.println("=================短信发送成功：" + status + "=================");

    }

    public static void sendSmsVerificationCode(String phoneNumber, String code) {
        if (CloudConstants.TXCLOUD.equals(platform)) {
            smsTxVerificationCodeClient(phoneNumber, code);
            return;
        }
        if (CloudConstants.ALIYUN.equals(platform)) {
            smsAliVerificationCodeClient(phoneNumber, code);
        }
    }

    /**
     * 发送验证码短信(只发送验证码短信)
     */
    private static void smsTxVerificationCodeClient(String phoneNumber, String code) {
        try {
            Credential cred = new Credential(secretId, secretKey);
            SendSmsResponse response = getSendSmsResponse(phoneNumber, code, cred);
            String status = response.getSendStatusSet()[0].getCode();
            System.out.println("=================短信发送成功：" + status + "=================");

        } catch (TencentCloudSDKException e) {
            throw new UtilException("腾讯短信发送错误：" + e.getMessage());
        }
    }

    /**
     * 发送验证码短信(只发送验证码短信)
     */
    private static void smsAliVerificationCodeClient(String phoneNumber, String code) {
        JSONObject params = new JSONObject();
        params.put("code", code);
        params.put("expireTime", expireTime);
        StaticCredentialProvider provider = StaticCredentialProvider.create(com.aliyun.auth.credentials.Credential.builder()
                .accessKeyId(secretId)
                .accessKeySecret(secretKey)
                .build());

        AsyncClient asyncClient = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com"))
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phoneNumber)
                .signName(signName)
                .templateCode(templateId)
                .templateParam(JSONObject.toJSONString(params))
                .build();

        CompletableFuture<com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse> response = asyncClient.sendSms(sendSmsRequest);
        com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new UtilException("阿里云短信发送错误：" + e.getMessage());
        }
        String json = new Gson().toJson(resp);
        asyncClient.close();
        // 发送结果
        String body = JSON.parseObject(json).getString("body");
        String message = JSON.parseObject(body).getString("message");
        System.out.println("=================短信发送结果：" + message + "=================");
    }

    private static SendSmsResponse getSendSmsResponse(String phoneNumber, String code, Credential cred) throws TencentCloudSDKException {
        SmsClient client = new SmsClient(cred, region);
        com.tencentcloudapi.sms.v20210111.models.SendSmsRequest req = new com.tencentcloudapi.sms.v20210111.models.SendSmsRequest();
        req.setSmsSdkAppId(sdkAppId);
        req.setSignName(signName);
        req.setTemplateId(templateId);
        String[] phoneNumbers = {phoneNumber};
        req.setPhoneNumberSet(phoneNumbers);
        String[] templateParams = {code,expireTime};
        req.setTemplateParamSet(templateParams);
        return client.SendSms(req);
    }
}
