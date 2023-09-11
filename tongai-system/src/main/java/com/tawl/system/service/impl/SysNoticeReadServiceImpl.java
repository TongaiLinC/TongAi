package com.tawl.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tawl.common.annotation.DataScope;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.SecurityUtils;
import com.tawl.system.domain.SysNoticeRead;
import com.tawl.system.mapper.SysNoticeReadMapper;
import com.tawl.system.service.ISysNoticeReadService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知公告阅读记录Service业务层处理
 * 
 * @author tongaikeji
 * @date 2023-09-03
 */
@Service
public class SysNoticeReadServiceImpl extends ServiceImpl<SysNoticeReadMapper, SysNoticeRead> implements ISysNoticeReadService
{
    /**
     * 查询通知公告阅读记录
     * 
     * @param id 通知公告阅读记录主键
     * @return 通知公告阅读记录
     */
    @Override
    public SysNoticeRead selectSysNoticeReadById(Long id)
    {
        return baseMapper.selectSysNoticeReadById(id);
    }

    /**
     * 查询通知公告阅读记录列表
     * 
     * @param sysNoticeRead 通知公告阅读记录
     * @return 通知公告阅读记录
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysNoticeRead> selectSysNoticeReadList(SysNoticeRead sysNoticeRead)
    {
        return baseMapper.selectSysNoticeReadList(sysNoticeRead);
    }

    /**
     * 新增通知公告阅读记录
     * 
     * @param sysNoticeRead 通知公告阅读记录
     * @return 结果
     */
    @Override
    public int insertSysNoticeRead(SysNoticeRead sysNoticeRead)
    {
        sysNoticeRead.setCreateTime(DateUtils.getNowDate());
        sysNoticeRead.setCreateBy(SecurityUtils.getUsername());
        return baseMapper.insertSysNoticeRead(sysNoticeRead);
    }

    /**
     * 修改通知公告阅读记录
     * 
     * @param sysNoticeRead 通知公告阅读记录
     * @return 结果
     */
    @Override
    public int updateSysNoticeRead(SysNoticeRead sysNoticeRead)
    {
        sysNoticeRead.setUpdateTime(DateUtils.getNowDate());
        sysNoticeRead.setUpdateBy(SecurityUtils.getUsername());
        return baseMapper.updateSysNoticeRead(sysNoticeRead);
    }

    /**
     * 批量删除通知公告阅读记录
     * 
     * @param ids 需要删除的通知公告阅读记录主键
     * @return 结果
     */
    @Override
    public int deleteSysNoticeReadByIds(Long[] ids)
    {
        return baseMapper.deleteSysNoticeReadByIds(ids);
    }

    /**
     * 删除通知公告阅读记录信息
     * 
     * @param id 通知公告阅读记录主键
     * @return 结果
     */
    @Override
    public int deleteSysNoticeReadById(Long id)
    {
        return baseMapper.deleteSysNoticeReadById(id);
    }

    /**
     * 获取通知公告未读数量
     * @return 未读消息数量
     */
    @Override
    public int getUnReadCount() {
        long receiverId = SecurityUtils.getUserId();
        return baseMapper.getUnReadCount(receiverId);
    }
}
