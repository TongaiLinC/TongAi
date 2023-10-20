package com.tawl.common.core.domain.model;

import lombok.Data;

import java.io.InputStream;

/**
 * 上传文件对象
 */
@Data
public class UpLoadFile {
    /**
     * 云服务平台
     */
    String platform;

    /**
     * 存储桶名称
     */
    String bucketName;

    /**
     * 存储桶区域
     */
    String bucketRegion;

    /**
     * 存储桶节点（阿里云）
     */
    String bucketEndpoint;

    /**
     * 存储桶路径（咧：xxx/xxx/a.png）
     */
    String filePath;

    /**
     * 文件输入流
     */
    InputStream fileInputStream;
}
