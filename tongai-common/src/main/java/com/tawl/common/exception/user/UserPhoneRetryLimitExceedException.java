package com.tawl.common.exception.user;

/**
 * 用户错误最大次数异常类
 * 
 * @author tongai
 */
public class UserPhoneRetryLimitExceedException extends UserException
{
    private static final long serialVersionUID = 1L;

    public UserPhoneRetryLimitExceedException(int retryLimitCount, int lockTime)
    {
        super("user.sms.retry.limit.exceed", new Object[] { retryLimitCount, lockTime });
    }
}
