package com.tawl.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tawl.system.domain.SysFileChunkInfo;

import java.util.List;

/**
 * 文件分片信息Service接口
 * 
 * @author tongaikeji
 * @date 2025-04-05
 */
public interface ISysFileChunkInfoService extends IService<SysFileChunkInfo>
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
     * 批量删除文件分片信息
     * 
     * @param ids 需要删除的文件分片信息主键集合
     * @return 结果
     */
    public int deleteSysFileChunkInfoByIds(Long[] ids);

    /**
     * 删除文件分片信息信息
     * 
     * @param id 文件分片信息主键
     * @return 结果
     */
    public int deleteSysFileChunkInfoById(Long id);

}