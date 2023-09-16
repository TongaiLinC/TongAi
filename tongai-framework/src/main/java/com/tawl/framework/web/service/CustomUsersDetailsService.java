package com.tawl.framework.web.service;

import com.tawl.common.exception.user.UserPhoneNotExistsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 自定义UserDetailsService方法，添加手机号登录
 */
public interface CustomUsersDetailsService extends UserDetailsService {

    /**
     * 通过手机号查询用户信息
     * @param phone 手机号
     * @return 结果
     */
    UserDetails loadUserByPhone(String phone) throws UserPhoneNotExistsException;
}
