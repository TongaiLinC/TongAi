package com.tawl.framework.web.service;

import com.tawl.common.constant.CacheConstants;
import com.tawl.common.constant.Constants;
import com.tawl.common.constant.UserConstants;
import com.tawl.common.core.domain.entity.SysUser;
import com.tawl.common.core.domain.model.LoginUser;
import com.tawl.common.core.redis.RedisCache;
import com.tawl.common.exception.ServiceException;
import com.tawl.common.exception.user.*;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.MessageUtils;
import com.tawl.common.utils.StringUtils;
import com.tawl.common.utils.ip.IpUtils;
import com.tawl.framework.manager.AsyncManager;
import com.tawl.framework.manager.factory.AsyncFactory;
import com.tawl.framework.security.authentication.PhoneAuthenticationToken;
import com.tawl.framework.security.context.AuthenticationContextHolder;
import com.tawl.system.service.ISysConfigService;
import com.tawl.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author tongai
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private CustomUsersDetailsService usersDetailsService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param phone    手机号
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String phone, String code, String uuid) {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password, phone);
        // 用户验证
        LoginUser loginUser;
        if (StringUtils.isEmpty(phone)) {
            loginUser = (LoginUser) loginByUserNameAndPassword(username, password, uuid).getPrincipal();
        } else {
            loginUser = (LoginUser) loginByPhone(phone, code);
        }

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 用户名密码登录
     */
    public Authentication loginByUserNameAndPassword(String username, String password, String uuid) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
    }

    /**
     * 验证码登录登录
     */
    private UserDetails loginByPhone(String phone, String code) {
        PhoneAuthenticationToken phoneAuthenticationToken = new PhoneAuthenticationToken(phone, code);
        AuthenticationContextHolder.setContext(phoneAuthenticationToken);
        UserDetails userDetails = usersDetailsService.loadUserByPhone(phone);
        if (userDetails == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            AuthenticationContextHolder.clearContext();
            throw new UserNotExistsException();
        }
        AuthenticationContextHolder.clearContext();
        return userDetails;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        // 当用户名不为空时，校验验证码
        if (StringUtils.isNotEmpty(username)) {
            boolean captchaEnabled = configService.selectCaptchaEnabled();
            if (captchaEnabled) {
                String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
                String captcha = redisCache.getCacheObject(verifyKey);
                redisCache.deleteObject(verifyKey);
                if (captcha == null) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                    throw new CaptchaExpireException();
                }
                if (!code.equals(captcha)) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                    throw new CaptchaException();
                }
            }
        }
    }

    /**
     * 登录前置校验
     *
     * @param username 用户名
     * @param password 用户密码
     * @param phone    用户手机号
     */
    public void loginPreCheck(String username, String password, String phone) {
        if (StringUtils.isEmpty(phone)) {
            // 用户名或密码为空 错误
            if ((StringUtils.isEmpty(username) || StringUtils.isEmpty(password))) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(StringUtils.isEmpty(username) ? phone : username,
                        Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
                throw new UserNotExistsException();
            }
            // 密码如果不在指定范围内 错误
            if ((password.length() < UserConstants.PASSWORD_MIN_LENGTH
                    || password.length() > UserConstants.PASSWORD_MAX_LENGTH)) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            // 用户名不在指定范围内 错误
            if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                    || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
        }
        // 手机号格式 错误
        if (StringUtils.isNotEmpty(phone) && (phone.length() < UserConstants.PHONE_NUMBER_MAX_LENGTH || !StringUtils.isMobile(phone))) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, MessageUtils.message("user.mobile.phone.number.not.valid")));
            throw new UserPhoneNotValidException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
