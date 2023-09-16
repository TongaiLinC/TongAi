package com.tawl.common.exception.user;

/**
 * 短信验证码失效异常类
 * 
 * @author tongai
 */
public class SmsCodeExpireException extends UserException
{
    private static final long serialVersionUID = 1L;

    public SmsCodeExpireException()
    {
        super("user.sms.code.expire", null);
    }
}
