package com.tawl.common.constant;

/**
 * 云服务常量
 *
 * @author tongai
 */
public class CloudConstants {

    /**
     * 阿里云
     */
    public static final String ALIYUN = "ali";

    /**
     * 腾讯云
     */
    public static final String TXCLOUD = "tx";

    /**
     * 七牛云
     */
    public static final String QINIU = "qn";

    /**
     * 华为云
     */
    public static final String HUAWEI = "huawei";

    /**
     * 腾讯云COS地址
     */
    public static final String TX_COS_URL = "https://%s.cos.%s.myqcloud.com/";

    /**
     * 腾讯云COS地址
     */
    public static final String ALI_OSS_URL = "https://%s.oss-cn-%s.aliyuncs.com/";

    /**
     * 获取完整的腾讯云COS地址
     *
     * @param bucketName   存储桶名称
     * @param bucketRegion 存储桶地区
     * @return 地址
     */
    public static String getTxCosUrl(String bucketName, String bucketRegion) {
        return String.format(TX_COS_URL,bucketName,bucketRegion);
    }

    /**
     * 获取完整的阿里云OSS地址
     *
     * @param bucketName   存储桶名称
     * @param bucketRegion 存储桶地区
     * @return 地址
     */
    public static String getAliOssUrl(String bucketName, String bucketRegion) {
        return String.format(ALI_OSS_URL, bucketName, bucketRegion);
    }


}
