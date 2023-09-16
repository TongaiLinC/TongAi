package com.tawl.common.exception.user;

/**
 * 用户手机号不正确或不符合规范异常类
 * 
 * @author tongai
 */
public class UserPhoneNotValidException extends UserException
{
    private static final long serialVersionUID = 1L;

    public UserPhoneNotValidException()
    {
        super("user.mobile.phone.number.not.valid", null);
    }
}
