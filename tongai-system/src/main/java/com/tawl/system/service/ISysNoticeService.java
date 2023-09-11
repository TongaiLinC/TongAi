package com.tawl.system.service;

import com.tawl.system.domain.SysNotice;

import java.util.List;

/**
 * 公告 服务层
 * 
 * @author tongai
 */
public interface ISysNoticeService
{
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表(发布者列表)
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 查询公告列表(用户列表)
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectReadNoticeList(SysNotice notice);

    /**
     * 查询公告列表(未读列表)
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectUnReadList(SysNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);
    
    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);

    /**
     * 修改阅读状态
     *
     * @param noticeId 公告ID
     */
    public void changeReadStatus(Long noticeId);

    /**
     * 设置为已读
     *
     * @param noticeIds 公告ID
     */
    void setAllNoticeRead(Long[] noticeIds);
}
