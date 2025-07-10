package com.tawl.common.utils.file;

import com.tawl.common.config.TongAiConfig;
import com.tawl.common.constant.Constants;
import com.tawl.common.utils.DateUtils;
import com.tawl.common.utils.StringUtils;
import com.tawl.common.utils.uuid.IdUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件处理工具类
 *
 * @author tongai
 */
public class FileUtils {
  public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

  /**
   * 输出指定文件的byte数组
   *
   * @param filePath 文件路径
   * @param os       输出流
   * @return
   */
  public static void writeBytes(String filePath, OutputStream os) throws IOException {
    FileInputStream fis = null;
    try {
      File file = new File(filePath);
      if (!file.exists()) {
        throw new FileNotFoundException(filePath);
      }
      fis = new FileInputStream(file);
      byte[] b = new byte[1024];
      int length;
      while ((length = fis.read(b)) > 0) {
        os.write(b, 0, length);
      }
    } catch (IOException e) {
      throw e;
    } finally {
      IOUtils.close(os);
      IOUtils.close(fis);
    }
  }

  /**
   * 写数据到文件中
   *
   * @param data 数据
   * @return 目标文件
   * @throws IOException IO异常
   */
  public static String writeImportBytes(byte[] data) throws IOException {
    return writeBytes(data, TongAiConfig.getImportPath());
  }

  /**
   * 写数据到文件中
   *
   * @param data      数据
   * @param uploadDir 目标文件
   * @return 目标文件
   * @throws IOException IO异常
   */
  public static String writeBytes(byte[] data, String uploadDir) throws IOException {
    FileOutputStream fos = null;
    String pathName = "";
    try {
      String extension = getFileExtendName(data);
      pathName = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension;
      File file = FileUploadUtils.getAbsoluteFile(uploadDir, pathName);
      fos = new FileOutputStream(file);
      fos.write(data);
    } finally {
      IOUtils.close(fos);
    }
    return FileUploadUtils.getPathFileName(uploadDir, pathName);
  }

  /**
   * 移除路径中的请求前缀片段
   *
   * @param filePath 文件路径
   * @return 移除后的文件路径
   */
  public static String stripPrefix(String filePath)
  {
    return StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX);
  }

  /**
   * 删除文件
   *
   * @param filePath 文件
   * @return
   */
  public static boolean deleteFile(String filePath) {
    boolean flag = false;
    File file = new File(filePath);
    System.out.println("================================" + filePath + "================================");
    // 路径为文件且不为空则进行删除
    if (file.isFile() && file.exists()) {
      flag = file.delete();
    }
    return flag;
  }

  /**
   * 删除文件或目录
   */
  public static boolean del(File file) {
    if (file == null || !file.exists()) {
      return false;
    }
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File f : files) {
          del(f);
        }
      }
    }
    return file.delete();
  }

  /**
   * 文件名称验证
   *
   * @param filename 文件名称
   * @return true 正常 false 非法
   */
  public static boolean isValidFilename(String filename) {
    return filename.matches(FILENAME_PATTERN);
  }

  /**
   * 检查文件是否可下载
   *
   * @param resource 需要下载的文件
   * @return true 正常 false 非法
   */
  public static boolean checkAllowDownload(String resource) {
    // 禁止目录上跳级别
    if (StringUtils.contains(resource, "..")) {
      return false;
    }

    // 检查允许下载的文件规则
    if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource))) {
      return true;
    }

    // 不在允许下载的文件规则
    return false;
  }

  /**
   * 下载文件名重新编码
   *
   * @param request  请求对象
   * @param fileName 文件名
   * @return 编码后的文件名
   */
  public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
    final String agent = request.getHeader("USER-AGENT");
    String filename = fileName;
    if (agent.contains("MSIE")) {
      // IE浏览器
      filename = URLEncoder.encode(filename, "utf-8");
      filename = filename.replace("+", " ");
    } else if (agent.contains("Firefox")) {
      // 火狐浏览器
      filename = new String(fileName.getBytes(), "ISO8859-1");
    } else if (agent.contains("Chrome")) {
      // google浏览器
      filename = URLEncoder.encode(filename, "utf-8");
    } else {
      // 其它浏览器
      filename = URLEncoder.encode(filename, "utf-8");
    }
    return filename;
  }

  /**
   * 下载文件名重新编码
   *
   * @param response     响应对象
   * @param realFileName 真实文件名
   */
  public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
    String percentEncodedFileName = percentEncode(realFileName);

    StringBuilder contentDispositionValue = new StringBuilder();
    contentDispositionValue.append("attachment; filename=")
            .append(percentEncodedFileName)
            .append(";")
            .append("filename*=")
            .append("utf-8''")
            .append(percentEncodedFileName);

    response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
    response.setHeader("Content-disposition", contentDispositionValue.toString());
    response.setHeader("download-filename", percentEncodedFileName);
  }

  /**
   * 百分号编码工具方法
   *
   * @param s 需要百分号编码的字符串
   * @return 百分号编码后的字符串
   */
  public static String percentEncode(String s) throws UnsupportedEncodingException {
    String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
    return encode.replaceAll("\\+", "%20");
  }

  /**
   * 获取图像后缀
   *
   * @param photoByte 图像数据
   * @return 后缀名
   */
  public static String getFileExtendName(byte[] photoByte) {
    String strFileExtendName = "jpg";
    if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
            && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
      strFileExtendName = "gif";
    } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
      strFileExtendName = "jpg";
    } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
      strFileExtendName = "bmp";
    } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
      strFileExtendName = "png";
    }
    return strFileExtendName;
  }

  /**
   * 获取文件扩展名
   */
  public static String extName(String filename) {
    if (filename == null) {
      return null;
    }
    int index = filename.lastIndexOf(".");
    if (index == -1) {
      return "";
    }
    return filename.substring(index + 1);
  }

  /**
   * 获取文件名称 /profile/upload/2022/04/16/tongai.png -- tongai.png
   *
   * @param fileName 路径名称
   * @return 没有文件路径的名称
   */
  public static String getName(String fileName) {
    if (fileName == null) {
      return null;
    }
    int lastUnixPos = fileName.lastIndexOf('/');
    int lastWindowsPos = fileName.lastIndexOf('\\');
    int index = Math.max(lastUnixPos, lastWindowsPos);
    return fileName.substring(index + 1);
  }

  /**
   * 获取不带后缀文件名称 /profile/upload/2022/04/16/tongai.png -- tongai
   *
   * @param fileName 路径名称
   * @return 没有文件路径和后缀的名称
   */
  public static String getNameNotSuffix(String fileName) {
    if (fileName == null) {
      return null;
    }
    return FilenameUtils.getBaseName(fileName);
  }

  /**
   * 获取文件的MD5值
   */
  public static String getFileMd5(InputStream inputStream) {
    try {
      return DigestUtils.md5Hex(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取文件的MD5值
   */
  public static String getFileMd5(File file) {
    try (InputStream inputStream = new FileInputStream(file)) {
      return getFileMd5(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 追加字节到文件
   */
  public static void appendBytes(File sourceFile, File targetFile) throws IOException {
    try (InputStream in = new FileInputStream(sourceFile);
         OutputStream out = new FileOutputStream(targetFile, true)) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
    }
  }

  /**
   * 获取文件的MIME类型
   */
  public static String getMimeType(String fileExt) {
    if (fileExt == null || fileExt.isEmpty()) {
      return "application/octet-stream";
    }
    fileExt = fileExt.toLowerCase();
    switch (fileExt) {
      case "jpg":
      case "jpeg":
        return "image/jpeg";
      case "png":
        return "image/png";
      case "gif":
        return "image/gif";
      case "bmp":
        return "image/bmp";
      case "mp4":
        return "video/mp4";
      case "webm":
        return "video/webm";
      case "ogg":
        return "video/ogg";
      case "pdf":
        return "application/pdf";
      case "doc":
      case "docx":
        return "application/msword";
      case "xls":
      case "xlsx":
        return "application/vnd.ms-excel";
      case "ppt":
      case "pptx":
        return "application/vnd.ms-powerpoint";
      case "txt":
        return "text/plain";
      case "zip":
        return "application/zip";
      case "rar":
        return "application/x-rar-compressed";
      default:
        return "application/octet-stream";
    }
  }
}