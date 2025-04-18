package com.tawl.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tawl.system.domain.SysFileChunkInfo;

import java.util.List;

/**
 * 文件分片信息Mapper接口
 * 
 * @author tongaikeji
 * @date 2025-04-05
 */
public interface SysFileChunkInfoMapper extends BaseMapper<SysFileChunkInfo>
{
    /**
     * 查询文件分片信息
     * 
     * @param id 文件分片信息主键
     * @return 文件分片信息
     */
    public SysFileChunkInfo selectSysFileChunkInfoById(Long id);

    /**
     * 查询文件分片信息列表
     * 
     * @param sysFileChunkInfo 文件分片信息
     * @return 文件分片信息集合
     */
    public List<SysFileChunkInfo> selectSysFileChunkInfoList(SysFileChunkInfo sysFileChunkInfo);

    /**
     * 新增文件分片信息
     * 
     * @param sysFileChunkInfo 文件分片信息
     * @return 结果
     */
    public int insertSysFileChunkInfo(SysFileChunkInfo sysFileChunkInfo);

    /**
     * 修改文件分片信息
     * 
     * @param sysFileChunkInfo 文件分片信息
     * @return 结果
     */
    public int updateSysFileChunkInfo(SysFileChunkInfo sysFileChunkInfo);

    /**
     * 删除文件分片信息
     * 
     * @param id 文件分片信息主键
     * @return 结果
     */
    public int deleteSysFileChunkInfoById(Long id);

    /**
     * 批量删除文件分片信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFileChunkInfoByIds(Long[] ids);
}