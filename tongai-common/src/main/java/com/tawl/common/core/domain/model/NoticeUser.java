package com.tawl.common.core.domain.model;

import lombok.Getter;

import java.io.Serializable;

/**
 * 用户对象 sys_user
 * 
 * @author tongai
 */
@Getter
public class NoticeUser implements Serializable
{

    /** 用户ID */
    private Long userId;

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "NoticeUser{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
