package com.tawl.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tawl.common.annotation.DataScope;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.SecurityUtils;
import com.tawl.system.domain.SysFileChunkInfo;
import com.tawl.system.mapper.SysFileChunkInfoMapper;
import com.tawl.system.service.ISysFileChunkInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件分片信息Service业务层处理
 * 
 * @author tongaikeji
 * @date 2025-04-05
 */
@Service
public class SysFileChunkInfoServiceImpl extends ServiceImpl<SysFileChunkInfoMapper, SysFileChunkInfo> implements ISysFileChunkInfoService
{
    /**
     * 查询文件分片信息
     * 
     * @param id 文件分片信息主键
     * @return 文件分片信息
     */
    @Override
    public SysFileChunkInfo selectSysFileChunkInfoById(Long id)
    {
        return baseMapper.selectSysFileChunkInfoById(id);
    }

    /**
     * 查询文件分片信息列表
     * 
     * @param sysFileChunkInfo 文件分片信息
     * @return 文件分片信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysFileChunkInfo> selectSysFileChunkInfoList(SysFileChunkInfo sysFileChunkInfo)
    {
        return baseMapper.selectSysFileChunkInfoList(sysFileChunkInfo);
    }

    /**
     * 新增文件分片信息
     * 
     * @param sysFileChunkInfo 文件分片信息
     * @return 结果
     */
    @Override
    public int insertSysFileChunkInfo(SysFileChunkInfo sysFileChunkInfo)
    {
        sysFileChunkInfo.setCreateTime(DateUtils.getNowDate());
        sysFileChunkInfo.setUserId(SecurityUtils.getUserId());
        sysFileChunkInfo.setDeptId(SecurityUtils.getDeptId());
        sysFileChunkInfo.setCreateBy(SecurityUtils.getUsername());
        return baseMapper.insertSysFileChunkInfo(sysFileChunkInfo);
    }

    /**
     * 修改文件分片信息
     * 
     * @param sysFileChunkInfo 文件分片信息
     * @return 结果
     */
    @Override
    public int updateSysFileChunkInfo(SysFileChunkInfo sysFileChunkInfo)
    {
        return baseMapper.updateSysFileChunkInfo(sysFileChunkInfo);
    }

    /**
     * 批量删除文件分片信息
     * 
     * @param ids 需要删除的文件分片信息主键
     * @return 结果
     */
    @Override
    public int deleteSysFileChunkInfoByIds(Long[] ids)
    {
        return baseMapper.deleteSysFileChunkInfoByIds(ids);
    }

    /**
     * 删除文件分片信息信息
     * 
     * @param id 文件分片信息主键
     * @return 结果
     */
    @Override
    public int deleteSysFileChunkInfoById(Long id)
    {
        return baseMapper.deleteSysFileChunkInfoById(id);
    }

}