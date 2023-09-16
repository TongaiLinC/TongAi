package com.tawl.framework.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class PhoneAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private String phoneNumber;
    private String code;

    public PhoneAuthenticationToken(String phoneNumber, String code) {
        super(null);
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.setAuthenticated(false);
    }

    public PhoneAuthenticationToken(String phoneNumber, String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phoneNumber = phoneNumber;
        this.code = code;
        super.setAuthenticated(true);
    }
    @Override
    public Object getPrincipal() {
        return phoneNumber;
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.code = null;
    }
}
