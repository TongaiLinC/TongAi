package com.tawl.framework.web.service;

import com.tawl.common.constant.CacheConstants;
import com.tawl.common.constant.Constants;
import com.tawl.common.core.redis.RedisCache;
import com.tawl.common.exception.user.SmsCodeException;
import com.tawl.common.exception.user.SmsCodeExpireException;
import com.tawl.common.exception.user.UserPhoneRetryLimitExceedException;
import com.tawl.common.utils.MessageUtils;
import com.tawl.common.utils.StringUtils;
import com.tawl.framework.manager.AsyncManager;
import com.tawl.framework.manager.factory.AsyncFactory;
import com.tawl.framework.security.context.AuthenticationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录短信验证码方法
 * 
 * @author tongai
 */
@Component
public class SysSmsCodeService
{
    @Autowired
    private RedisCache redisCache;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount;

    @Value(value = "${user.password.lockTime}")
    private int lockTime;

    /**
     * 登录验证码错误次数缓存键名
     * 
     * @param phone 手机号
     * @return 缓存键key
     */
    private String getCacheKey(String phone)
    {
        return CacheConstants.SMS_ERR_CNT_KEY + phone;
    }

    public void validate()
    {
        Authentication phoneAuthenticationToken = AuthenticationContextHolder.getContext();
        String phone = phoneAuthenticationToken.getName();
        String code = phoneAuthenticationToken.getCredentials().toString();

        Integer retryCount = redisCache.getCacheObject(getCacheKey(phone));

        if (retryCount == null)
        {
            retryCount = 0;
        }

        if (retryCount >= maxRetryCount)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, MessageUtils.message("user.sms.retry.limit.exceed", maxRetryCount, lockTime)));
            throw new UserPhoneRetryLimitExceedException(maxRetryCount, lockTime);
        }

        if (!matches(phone, code))
        {
            retryCount = retryCount + 1;
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, MessageUtils.message("user.sms.retry.limit.count", retryCount)));
            redisCache.setCacheObject(getCacheKey(phone), retryCount, lockTime, TimeUnit.MINUTES);
            throw new SmsCodeException();
        }
        else
        {
            clearLoginRecordCache(phone);
        }
    }

    public boolean matches(String phone, String code)
    {
        String verifyKey = CacheConstants.SMS_CODE_KEY + StringUtils.nvl(phone, "");
        String smsCode = redisCache.getCacheObject(verifyKey);
        if (smsCode == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, MessageUtils.message("user.sms.code.expire")));
            throw new SmsCodeExpireException();
        }
        return smsCode.equalsIgnoreCase(code);
    }

    public void clearLoginRecordCache(String phone)
    {
        if (redisCache.hasKey(getCacheKey(phone)))
        {
            redisCache.deleteObject(getCacheKey(phone));
        }
    }
}
