package com.tawl.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tawl.common.core.domain.AjaxResult;
import com.tawl.system.domain.SysFileChunkInfo;
import com.tawl.system.domain.SysFileInfo;

import java.util.List;

/**
 * 文件信息Service接口
 * 
 * @author tongaikeji
 * @date 2023-10-27
 */
public interface ISysFileInfoService extends IService<SysFileInfo>
{
    /**
     * 查询文件信息
     * 
     * @param fileId 文件信息主键
     * @return 文件信息
     */
    public SysFileInfo selectSysFileInfoByFileId(Long fileId);

    /**
     * 查询文件信息列表
     * 
     * @param sysFileInfo 文件信息
     * @return 文件信息集合
     */
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo);

    /**
     * 新增文件信息
     * 
     * @param sysFileInfo 文件信息
     * @return 结果
     */
    public int insertSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 修改文件信息
     * 
     * @param sysFileInfo 文件信息
     * @return 结果
     */
    public int updateSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 批量删除文件信息
     * 
     * @param fileIds 需要删除的文件信息主键集合
     * @return 结果
     */
    public int deleteSysFileInfoByFileIds(Long[] fileIds);

    /**
     * 删除文件信息信息
     * 
     * @param fileId 文件信息主键
     * @return 结果
     */
    public int deleteSysFileInfoByFileId(Long fileId);
   
    /**
     * 通过唯一编号查询文件信息
     *
     * @param identifier 文件唯一编码
     * @return 文件信息
     */
    public SysFileInfo selectSysFileInfoByIdentifier(String identifier);

    /**
     * 检查文件是否已上传过
     *
     * @param sysFileInfo 文件信息
     * @return 结果
     */
    public AjaxResult checkFile(SysFileInfo sysFileInfo);

    /**
     * 上传文件
     *
     * @param fileInfo 文件信息
     * @return 结果
     */
    public AjaxResult upload(SysFileInfo fileInfo);

    /**
     * 分片上传文件
     *
     * @param fileChunkInfo 文件信息
     * @param fileDirPath 文件路径
     * @return 结果
     */
    public AjaxResult uploadChunk(SysFileChunkInfo fileChunkInfo,String fileDirPath);

    /**
     * 分片合并
     *
     * @param fileChunkInfo 文件信息
     * @param fileDirPath 文件路径
     * @return 结果
     */
    public AjaxResult mergeChunks(SysFileChunkInfo fileChunkInfo, String fileDirPath);

}