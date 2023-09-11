package com.tawl.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tawl.system.domain.SysNoticeRead;

import java.util.List;

/**
 * 通知公告阅读记录Service接口
 * 
 * @author tongaikeji
 * @date 2023-09-03
 */
public interface ISysNoticeReadService extends IService<SysNoticeRead>
{
    /**
     * 查询通知公告阅读记录
     * 
     * @param id 通知公告阅读记录主键
     * @return 通知公告阅读记录
     */
    public SysNoticeRead selectSysNoticeReadById(Long id);

    /**
     * 查询通知公告阅读记录列表
     * 
     * @param sysNoticeRead 通知公告阅读记录
     * @return 通知公告阅读记录集合
     */
    public List<SysNoticeRead> selectSysNoticeReadList(SysNoticeRead sysNoticeRead);

    /**
     * 新增通知公告阅读记录
     * 
     * @param sysNoticeRead 通知公告阅读记录
     * @return 结果
     */
    public int insertSysNoticeRead(SysNoticeRead sysNoticeRead);

    /**
     * 修改通知公告阅读记录
     * 
     * @param sysNoticeRead 通知公告阅读记录
     * @return 结果
     */
    public int updateSysNoticeRead(SysNoticeRead sysNoticeRead);

    /**
     * 批量删除通知公告阅读记录
     * 
     * @param ids 需要删除的通知公告阅读记录主键集合
     * @return 结果
     */
    public int deleteSysNoticeReadByIds(Long[] ids);

    /**
     * 删除通知公告阅读记录信息
     * 
     * @param id 通知公告阅读记录主键
     * @return 结果
     */
    public int deleteSysNoticeReadById(Long id);

    /**
     * 获取通知公告未读数量
     * @return
     */
    int getUnReadCount();
}
