package com.tawl.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tawl.system.domain.SysNoticeRead;

import java.util.List;

/**
 * 通知公告阅读记录Mapper接口
 *
 * @author tongaikeji
 * @date 2023-09-03
 */
public interface SysNoticeReadMapper extends BaseMapper<SysNoticeRead> {
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
     * 删除通知公告阅读记录
     *
     * @param id 通知公告阅读记录主键
     * @return 结果
     */
    public int deleteSysNoticeReadById(Long id);

    /**
     * 批量删除通知公告阅读记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysNoticeReadByIds(Long[] ids);

    /**
     * 获取通知公告未读数量
     *
     * @param receiverId 接收者Id
     * @return 未读数量
     */
    int getUnReadCount(long receiverId);
}
