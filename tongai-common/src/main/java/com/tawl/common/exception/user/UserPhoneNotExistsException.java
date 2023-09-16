package com.tawl.common.exception.user;

import org.springframework.security.core.AuthenticationException;

public class UserPhoneNotExistsException extends AuthenticationException {
    public UserPhoneNotExistsException(String msg) {
        super(msg);
    }

    public UserPhoneNotExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}