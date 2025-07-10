package com.tawl.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tawl.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 通知公告表 sys_notice
 *
 * @author tongai
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNotice extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 默认通知用户为全体用户
     */
    public static final String DEFAULT_USER_IDS = "0";

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;

    /**
     * 通知接收人员ID（0-所有人员）
     */
    private String userIds;

    /**
     * 是否已读（0未读 1已读）
     */
    private String isRead;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 重要等级
     */
    private String weight;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /**
     * 发布者
     */
    private String publishUser;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("noticeId", getNoticeId())
                .append("noticeTitle", getNoticeTitle())
                .append("noticeType", getNoticeType())
                .append("noticeContent", getNoticeContent())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("isRead", getIsRead())
                .toString();
    }
}

