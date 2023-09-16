package com.tawl.common.exception.user;

/**
 * 短信验证码错误异常类
 * 
 * @author tongai
 */
public class SmsCodeException extends UserException
{
    private static final long serialVersionUID = 1L;

    public SmsCodeException()
    {
        super("user.sms.code.error", null);
    }
}
