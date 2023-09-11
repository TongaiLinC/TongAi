package com.tawl.system.domain;

import com.tawl.common.annotation.Excel;
import com.tawl.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 通知公告阅读记录对象 sys_notice_read
 * 
 * @author tongaikeji
 * @date 2023-09-03
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysNoticeRead extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID编号 */
    private Long id;

    /** 通知ID */
    @Excel(name = "通知ID")
    private Long noticeId;

    /** 是否已读0-未读，1-已读（当通知/公告为非全体通知时使用） */
    @Excel(name = "是否已读0-未读，1-已读", readConverterExp = "当=通知/公告为非全体通知时使用")
    private String isRead;

    /** 通知接收者ID（0-全体成员） */
    @Excel(name = "通知接收者ID", readConverterExp = "0=-全体成员")
    private Long receiverId;

}
