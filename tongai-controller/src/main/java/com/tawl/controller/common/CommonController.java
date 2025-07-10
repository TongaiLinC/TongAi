package com.tawl.controller.common;

import com.tawl.common.config.TongAiConfig;
import com.tawl.common.core.domain.AjaxResult;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.SecurityUtils;
import com.tawl.common.utils.StringUtils;
import com.tawl.common.utils.cloud.CloudUtils;
import com.tawl.common.utils.file.FileUploadUtils;
import com.tawl.common.utils.file.FileUtils;
import com.tawl.framework.config.ServerConfig;
import com.tawl.system.domain.SysFileChunkInfo;
import com.tawl.system.domain.SysFileInfo;
import com.tawl.system.service.ISysConfigService;
import com.tawl.system.service.ISysFileInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 *
 * @author tongai
 */
@RestController
@RequestMapping("/common")
public class CommonController {
  private static final Logger log = LoggerFactory.getLogger(CommonController.class);

  @Autowired
  private ServerConfig serverConfig;

  @Autowired
  private ISysConfigService sysConfigService;

  @Autowired
  private ISysFileInfoService sysFileInfoService;

  private static final String FILE_DELIMETER = ",";

  /**
   * 通用下载请求
   *
   * @param fileName 文件名称
   * @param delete   是否删除
   */
  @GetMapping("/download")
  public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
    try {
      if (!FileUtils.checkAllowDownload(fileName)) {
        throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
      }
      String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
      String filePath = TongAiConfig.getDownloadPath() + fileName;

      response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
      FileUtils.setAttachmentResponseHeader(response, realFileName);
      FileUtils.writeBytes(filePath, response.getOutputStream());
      if (delete) {
        FileUtils.deleteFile(filePath);
      }
    } catch (Exception e) {
      log.error("下载文件失败", e);
    }
  }

  /**
   * 通用上传请求（单个）
   */
  @PostMapping("/upload")
  public AjaxResult uploadFile(SysFileInfo fileInfo) throws Exception {
    if (fileInfo.getFile().isEmpty()) {
      return AjaxResult.error("文件不能为空");
    }
    String url = "";
    String fileName = fileInfo.getFile().getOriginalFilename();
    AjaxResult ajax = AjaxResult.success();
    // 生成文件唯一标识
    String identifier = FileUtils.getFileMd5(fileInfo.getFile().getInputStream());
    // 检查是否已存在
    SysFileInfo existFile = sysFileInfoService.selectSysFileInfoByIdentifier(identifier);
    if (existFile != null && existFile.getComplete()) {
      ajax.put("identifier", existFile.getIdentifier());
      ajax.put("url", existFile.getFilePath());
      ajax.put("filename", existFile.getFileName());
      return ajax;
    }
    String cloudStorageEnabled = sysConfigService.selectConfigByKey("sys.cloud.storageEnabled");
    // 是否开启云存储
    if (cloudStorageEnabled.equals("true")) {
      String filePath = SecurityUtils.getUsername() + "/" + DateUtils.getDate() + "/" + fileName;
      // 上传文件并返回文件路径
      url = CloudUtils.uploadFile(fileInfo.getFile().getInputStream(), filePath);
      fileInfo.setFilePath(url);
      fileInfo.setIdentifier(identifier);
      fileInfo.setComplete(true);
      fileInfo.setFileName(fileName);
    } else {
      try {
        // 上传文件路径
        String filePath = TongAiConfig.getUploadPath();
        // 上传并返回新文件名称
        fileName = FileUploadUtils.upload(filePath, fileInfo.getFile());
        url = serverConfig.getUrl() + fileName;
        fileInfo.setFilePath(fileName);
        fileInfo.setIdentifier(identifier);
        fileInfo.setComplete(true);
        ajax.put("filePath", fileName);
        ajax.put("fileName", FileUtils.getNameNotSuffix(fileName));
        // 保存文件信息
        sysFileInfoService.insertSysFileInfo(fileInfo);
      } catch (Exception e) {
        e.printStackTrace();
        return AjaxResult.error("文件上传失败");
      }
    }
    ajax.put("url", url);
    return ajax;
  }

  /**
   * 通用上传请求（多个）
   */
  @PostMapping("/uploads")
  public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception {
    try {
      // 上传文件路径
      String filePath = TongAiConfig.getUploadPath();
      List<String> urls = new ArrayList<String>();
      List<String> fileNames = new ArrayList<String>();
      List<String> newFileNames = new ArrayList<String>();
      List<String> originalFilenames = new ArrayList<String>();
      for (MultipartFile file : files) {
        // 上传并返回新文件名称
        String fileName = FileUploadUtils.upload(filePath, file);
        String url = serverConfig.getUrl() + fileName;
        urls.add(url);
        fileNames.add(fileName);
        newFileNames.add(FileUtils.getName(fileName));
        originalFilenames.add(file.getOriginalFilename());
      }
      AjaxResult ajax = AjaxResult.success();
      ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
      ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
      ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
      ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
      return ajax;
    } catch (Exception e) {
      return AjaxResult.error(e.getMessage());
    }
  }

  /**
   * 本地资源通用下载
   */
  @GetMapping("/download/resource")
  public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    try {
      if (!FileUtils.checkAllowDownload(resource)) {
        throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
      }
      // 本地资源路径
      String localPath = TongAiConfig.getProfile();
      // 数据库资源地址
      String downloadPath = localPath + FileUtils.stripPrefix(resource);
      // 下载名称
      String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
      response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
      FileUtils.setAttachmentResponseHeader(response, downloadName);
      FileUtils.writeBytes(downloadPath, response.getOutputStream());
    } catch (Exception e) {
      log.error("下载文件失败", e);
    }
  }

  /**
   * 检查文件是否存在，用于秒传和断点续传
   */
  @PostMapping("/checkFile")
  public AjaxResult checkFile(@RequestBody SysFileInfo fileInfo) {
    return sysFileInfoService.checkFile(fileInfo);
  }

  /**
   * 通用上传请求（分片上传）
   */
  @PostMapping("/uploadChunk")
  public AjaxResult uploadChunk(SysFileChunkInfo fileChunkInfo) {
    return sysFileInfoService.uploadChunk(fileChunkInfo, TongAiConfig.getUploadPath());
  }

  /**
   * 通用上传请求（合并分片）
   */
  @PostMapping("/mergeChunks")
  public AjaxResult mergeChunks(@RequestBody SysFileChunkInfo fileChunkInfo) {
    return sysFileInfoService.mergeChunks(fileChunkInfo, TongAiConfig.getUploadPath());
  }

}