package com.tawl.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tawl.system.domain.SysFileInfo;

import java.util.List;

/**
 * 文件管理Mapper接口
 * 
 * @author tongaikeji
 * @date 2023-10-27
 */
public interface SysFileInfoMapper extends BaseMapper<SysFileInfo>
{
    /**
     * 查询文件管理
     * 
     * @param fileId 文件管理主键
     * @return 文件管理
     */
    public SysFileInfo selectSysFileInfoByFileId(Long fileId);

    /**
     * 查询文件管理列表
     * 
     * @param sysFileInfo 文件管理
     * @return 文件管理集合
     */
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo);

    /**
     * 新增文件管理
     * 
     * @param sysFileInfo 文件管理
     * @return 结果
     */
    public int insertSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 修改文件管理
     * 
     * @param sysFileInfo 文件管理
     * @return 结果
     */
    public int updateSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 删除文件管理
     * 
     * @param fileId 文件管理主键
     * @return 结果
     */
    public int deleteSysFileInfoByFileId(Long fileId);

    /**
     * 批量删除文件管理
     * 
     * @param fileIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFileInfoByFileIds(Long[] fileIds);
}
