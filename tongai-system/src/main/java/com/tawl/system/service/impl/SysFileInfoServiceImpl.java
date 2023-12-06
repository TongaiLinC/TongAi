package com.tawl.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tawl.common.annotation.DataScope;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.SecurityUtils;
import com.tawl.system.domain.SysFileInfo;
import com.tawl.system.mapper.SysFileInfoMapper;
import com.tawl.system.service.ISysFileInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件管理Service业务层处理
 * 
 * @author tongaikeji
 * @date 2023-10-27
 */
@Service
public class SysFileInfoServiceImpl extends ServiceImpl<SysFileInfoMapper,SysFileInfo> implements ISysFileInfoService
{
    /**
     * 查询文件管理
     * 
     * @param fileId 文件管理主键
     * @return 文件管理
     */
    @Override
    public SysFileInfo selectSysFileInfoByFileId(Long fileId)
    {
        return baseMapper.selectSysFileInfoByFileId(fileId);
    }

    /**
     * 查询文件管理列表
     * 
     * @param sysFileInfo 文件管理
     * @return 文件管理
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo)
    {
        return baseMapper.selectSysFileInfoList(sysFileInfo);
    }

    /**
     * 新增文件管理
     * 
     * @param sysFileInfo 文件管理
     * @return 结果
     */
    @Override
    public int insertSysFileInfo(SysFileInfo sysFileInfo)
    {
        sysFileInfo.setCreateTime(DateUtils.getNowDate());
        sysFileInfo.setUserId(SecurityUtils.getUserId());
        sysFileInfo.setDeptId(SecurityUtils.getDeptId());
        sysFileInfo.setCreateBy(SecurityUtils.getUsername());
        return baseMapper.insertSysFileInfo(sysFileInfo);
    }

    /**
     * 修改文件管理
     * 
     * @param sysFileInfo 文件管理
     * @return 结果
     */
    @Override
    public int updateSysFileInfo(SysFileInfo sysFileInfo)
    {
        sysFileInfo.setUpdateTime(DateUtils.getNowDate());
        sysFileInfo.setUpdateBy(SecurityUtils.getUsername());
        return baseMapper.updateSysFileInfo(sysFileInfo);
    }

    /**
     * 批量删除文件管理
     * 
     * @param fileIds 需要删除的文件管理主键
     * @return 结果
     */
    @Override
    public int deleteSysFileInfoByFileIds(Long[] fileIds)
    {
        return baseMapper.deleteSysFileInfoByFileIds(fileIds);
    }

    /**
     * 删除文件管理信息
     * 
     * @param fileId 文件管理主键
     * @return 结果
     */
    @Override
    public int deleteSysFileInfoByFileId(Long fileId)
    {
        return baseMapper.deleteSysFileInfoByFileId(fileId);
    }

}
