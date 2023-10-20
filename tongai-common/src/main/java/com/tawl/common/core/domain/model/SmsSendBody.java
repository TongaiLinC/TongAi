package com.tawl.common.core.domain.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

/**
 * 发送短信参数对象
 */
@Getter
public class SmsSendBody {

    /**
     * 云服务平台信息
     * 腾讯云：tx  阿里云: al  七牛云：qn
     */
    private String platform;

    /**
     * 应用ID（腾讯云短信使用）
     */
    private String sdkAppId;

    /**
     * 短信验证码签名名称
     */
    private String signName;

    /**
     * 短信验证码模板ID
     */
    private String templateId;

    /**
     * 手机号列表（腾讯云短信使用 例：{ 15531111111 }）
     */
    private String[] phoneNumbers;

    /**
     * 短信参数（腾讯云短信使用，例：{ 1111, 10 }）
     */
    private String[] params;

    /**
     * 手机号（阿里云短信使用 例：15531111111）
     */
    private String phoneNumber;

    /**
     * 短信参数（阿里云短信使用，例：{"code":"1111","expireTime": 10}）
     */
    private JSONObject param;

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getParam() {
        return JSONObject.toJSONString(param);
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
