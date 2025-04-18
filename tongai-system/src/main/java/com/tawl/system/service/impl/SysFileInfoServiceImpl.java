package com.tawl.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tawl.common.annotation.DataScope;
import com.tawl.common.config.TongAiConfig;
import com.tawl.common.constant.Constants;
import com.tawl.common.core.domain.AjaxResult;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.SecurityUtils;
import com.tawl.common.utils.file.FileUtils;
import com.tawl.common.utils.uuid.UUID;
import com.tawl.system.domain.SysFileChunkInfo;
import com.tawl.system.domain.SysFileInfo;
import com.tawl.system.mapper.SysFileInfoMapper;
import com.tawl.system.service.ISysFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件管理Service业务层处理
 *
 * @author tongaikeji
 * @date 2023-10-27
 */
@Service
public class SysFileInfoServiceImpl extends ServiceImpl<SysFileInfoMapper, SysFileInfo> implements ISysFileInfoService {
  @Autowired
  private SysFileChunkInfoServiceImpl fileChunkInfoService;
  @Autowired
  private TongAiConfig tongAiConfig;

  /**
   * 查询文件管理
   *
   * @param fileId 文件管理主键
   * @return 文件管理
   */
  @Override
  public SysFileInfo selectSysFileInfoByFileId(Long fileId) {
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
  public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo) {
    return baseMapper.selectSysFileInfoList(sysFileInfo);
  }

  /**
   * 新增文件管理
   *
   * @param sysFileInfo 文件管理
   * @return 结果
   */
  @Override
  public int insertSysFileInfo(SysFileInfo sysFileInfo) {
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
  public int updateSysFileInfo(SysFileInfo sysFileInfo) {
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
  public int deleteSysFileInfoByFileIds(Long[] fileIds) {
    // 通过ID查询文件信息
    if (fileIds.length > 1) {
      for (Long fileId : fileIds) {
        SysFileInfo fileInfo = baseMapper.selectSysFileInfoByFileId(fileId);
        if (fileInfo != null) {
          // 删除文件
          FileUtils.deleteFile(fileInfo.getFilePath().replace(TongAiConfig.getProfile(), Constants.RESOURCE_PREFIX));
        }
      }
    } else {
      SysFileInfo fileInfo = baseMapper.selectSysFileInfoByFileId(fileIds[0]);
      if (fileInfo != null) {
        // 删除文件
        FileUtils.deleteFile(fileInfo.getFilePath().replace(Constants.RESOURCE_PREFIX,TongAiConfig.getProfile()));
      }
    }
    return baseMapper.deleteSysFileInfoByFileIds(fileIds);
  }

  /**
   * 删除文件管理信息
   *
   * @param fileId 文件管理主键
   * @return 结果
   */
  @Override
  public int deleteSysFileInfoByFileId(Long fileId) {
    return baseMapper.deleteSysFileInfoByFileId(fileId);
  }

  /**
   * 通过唯一标识查询文件信息
   *
   * @param identifier 文件信息唯一标识
   * @return 文件信息
   */
  @Override
  public SysFileInfo selectSysFileInfoByIdentifier(String identifier) {
    return baseMapper.selectSysFileInfoByIdentifier(identifier);
  }

  /**
   * 检查文件是否已上传过
   *
   * @param sysFileInfo 文件信息
   * @return 结果
   */
  public AjaxResult checkFile(SysFileInfo sysFileInfo) {
    AjaxResult result = AjaxResult.success();
    SysFileInfo fileInfo = baseMapper.selectSysFileInfoByIdentifier(sysFileInfo.getIdentifier());
    if (fileInfo != null && fileInfo.getComplete()) {
      result.put("uploaded", true);
      result.put("url", fileInfo.getFilePath());
      result.put("fileName", fileInfo.getFileName());
      return result;
    }
    // 检查是否有分片上传记录
    List<SysFileChunkInfo> chunkInfoList = fileChunkInfoService.list(new QueryWrapper<SysFileChunkInfo>().eq("identifier", sysFileInfo.getIdentifier()));
    if (!chunkInfoList.isEmpty()) {
      List<Long> uploadedChunks = chunkInfoList.stream().map(SysFileChunkInfo::getChunkNumber).collect(Collectors.toList());
      result.put("uploaded", false);
      result.put("uploadedChunks", uploadedChunks);
      return result;
    }

    result.put("uploaded", false);
    result.put("uploadedChunks", new ArrayList<>());
    return result;
  }

  @Override
  public AjaxResult upload(SysFileInfo fileInfo) {
    if (fileInfo.getFile().isEmpty()) {
      return AjaxResult.error("文件不能为空");
    }
    AjaxResult ajax = AjaxResult.success();
    try {
      // 生成文件唯一标识
      String identifier = FileUtils.getFileMd5(fileInfo.getFile().getInputStream());
      // 检查是否已存在
      SysFileInfo existFile = baseMapper.selectSysFileInfoByIdentifier(identifier);
      if (existFile != null && existFile.getComplete()) {
        ajax.put("url", existFile.getFilePath());
        ajax.put("fileName", existFile.getFileName());
        return ajax;
      }

    } catch (IOException e) {
      e.printStackTrace();
      return AjaxResult.error("文件上传失败");
    }
    return ajax;
  }

  @Override
  public AjaxResult uploadChunk(SysFileChunkInfo fileChunkInfo, String fileDirPath) {
    AjaxResult ajax = AjaxResult.success();
    MultipartFile file = fileChunkInfo.getFile();
    if (file.isEmpty()) {
      return AjaxResult.error("文件不能为空");
    }

    try {
      // 创建分片目录
      File chunkDir = new File(fileDirPath + "/" + fileChunkInfo.getIdentifier());
      if (!chunkDir.exists()) {
        chunkDir.mkdirs();
      }

      // 保存分片
      String chunkFilename = fileChunkInfo.getChunkNumber() + ".part";
      File chunkFile = new File(chunkDir, chunkFilename);
      file.transferTo(chunkFile);

      // 保存分片信息
      SysFileChunkInfo chunkInfo = new SysFileChunkInfo();
      chunkInfo.setChunkNumber(fileChunkInfo.getChunkNumber());
      chunkInfo.setChunkSize(fileChunkInfo.getChunkSize());
      chunkInfo.setIdentifier(fileChunkInfo.getIdentifier());
      chunkInfo.setRelativePath(fileDirPath + "/" + chunkDir.getName() + "/" + chunkFilename);
      chunkInfo.setTotalChunks(fileChunkInfo.getTotalChunks());
      fileChunkInfoService.insertSysFileChunkInfo(chunkInfo);

      ajax.put("chunkNumber", chunkInfo.getChunkNumber());
      return ajax;
    } catch (IOException e) {
      e.printStackTrace();
      return AjaxResult.error("分片上传失败");
    }
  }

  @Override
  public AjaxResult mergeChunks(SysFileChunkInfo fileChunkInfo, String fileDirPath) {
    try {
      // 检查所有分片是否已上传完成
      List<SysFileChunkInfo> chunkInfoList = fileChunkInfoService.list(new QueryWrapper<SysFileChunkInfo>().eq(
              "identifier", fileChunkInfo.getIdentifier()).orderByAsc("chunk_number"));

      if (chunkInfoList.size() != (fileChunkInfo.getTotalChunks())) {
        return AjaxResult.error("分片数量不匹配");
      }

      // 创建合并后的文件
      String dateDir = DateUtils.dateTimeNow("yyyy/MM/dd");
      File dir = new File(fileDirPath +"/"+ dateDir);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      String fileExt = FileUtils.extName(fileChunkInfo.getFileName());
      String fileName = UUID.fastUUID() + "." + fileExt;
      String filePath = dateDir + "/" + fileName;
      File destFile = new File(fileDirPath + "/" + filePath);

      // 合并分片
      for (SysFileChunkInfo chunkInfo : chunkInfoList) {
        File chunkFile = new File(chunkInfo.getRelativePath());
        FileUtils.appendBytes(chunkFile, destFile);
      }

      // 保存文件信息
      SysFileInfo fileInfo = new SysFileInfo();
      fileInfo.setFileName(fileChunkInfo.getFileName());
      fileInfo.setIdentifier(fileChunkInfo.getIdentifier());
      fileInfo.setFilePath(filePath);
      fileInfo.setFileSize(fileChunkInfo.getFileSize());
      fileInfo.setFileType(fileExt);
      fileInfo.setMimeType(FileUtils.getMimeType(fileExt));
      fileInfo.setComplete(true);
      fileInfo.setFilePath("/profile/upload/" + filePath);
      fileInfo.setCreateTime(DateUtils.getNowDate());
      fileInfo.setUserId(SecurityUtils.getUserId());
      fileInfo.setDeptId(SecurityUtils.getDeptId());
      fileInfo.setCreateBy(SecurityUtils.getUsername());
      baseMapper.insertSysFileInfo(fileInfo);

      // 删除分片和分片信息
      FileUtils.del(new File(fileDirPath+ "/" + fileChunkInfo.getIdentifier()));
      fileChunkInfoService.remove(new QueryWrapper<SysFileChunkInfo>().eq("identifier", fileChunkInfo.getIdentifier()));

      AjaxResult ajax = AjaxResult.success();
      ajax.put("url", fileInfo.getFilePath());
      ajax.put("filename", fileChunkInfo.getFileName());
      return ajax;
    } catch (Exception e) {
      e.printStackTrace();
      return AjaxResult.error("文件合并失败");
    }
  }
}