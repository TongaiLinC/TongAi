package com.tawl.common.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.google.gson.Gson;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import com.tawl.common.constant.CloudConstants;
import com.tawl.common.core.domain.model.SmsSendBody;
import com.tawl.common.core.domain.model.UpLoadFile;
import com.tawl.common.exception.ServiceException;
import com.tawl.common.exception.UtilException;
import com.tawl.common.utils.StringUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 腾讯云配置
 *
 * @author tongai
 */
@Log4j2
@Configuration
public class CloudConfig {
    /**
     * 腾讯云API调用秘钥ID
     */
    private static String secretId;

    /**
     * 腾讯云API调用秘钥Key
     */
    private static String secretKey;

    /**
     * 地域信息
     */
    private static String region;

    /**
     * 云服务平台信息
     * 腾讯云：tx  阿里云: al  七牛云：qn
     */
    private static String platform;

    /**
     * 应用ID
     */
    private static String sdkAppId;

    /**
     * 短信验证码模板ID
     */
    private static String templateId;

    /**
     * 短信验证码签名名称
     */
    private static String signName;

    /**
     * 短信验证码过期时间(分钟)
     */
    private static String expireTime;

    /**
     * 存储桶名称
     */
    private static String bucketName;

    /**
     * 存储桶地域
     */
    private static String bucketRegion;

    /**
     * 存储桶节点区域（阿里云OSS）
     */
    private static String bucketEndpoint;

    @Value("${cloud.secretId}")
    public void setSecretId(String secretId) {
        CloudConfig.secretId = secretId;
    }

    @Value("${cloud.secretKey}")
    public void setSecretKey(String secretKey) {
        CloudConfig.secretKey = secretKey;
    }

    @Value("${cloud.sms.region}")
    public void setRegion(String region) {
        CloudConfig.region = region;
    }

    @Value("${cloud.platform}")
    public void setPlatform(String platform) {
        CloudConfig.platform = platform;
    }

    @Value("${cloud.sms.sdkAppId}")
    public void setSdkAppId(String sdkAppId) {
        CloudConfig.sdkAppId = sdkAppId;
    }

    @Value("${cloud.sms.templateId}")
    public void setTemplateId(String templateId) {
        CloudConfig.templateId = templateId;
    }

    @Value("${cloud.sms.signName}")
    public void setSignName(String signName) {
        CloudConfig.signName = signName;
    }

    @Value("${cloud.sms.expireTime}")
    public void setExpireTime(String expireTime) {
        CloudConfig.expireTime = expireTime;
    }

    @Value("${cloud.cos.bucketName}")
    public void setBucketName(String bucketName) {
        CloudConfig.bucketName = bucketName;
    }

    @Value("${cloud.cos.bucketRegion}")
    public void setBucketRegion(String bucketRegion) {
        CloudConfig.bucketRegion = bucketRegion;
    }

    @Value("${cloud.cos.bucketEndpoint}")
    public void setBucketEndpoint(String bucketEndpoint) {
        CloudConfig.bucketEndpoint = bucketEndpoint;
    }

    /**
     * 快捷发送短信验证码
     *
     * @param phoneNumber 手机号
     * @param code        验证码
     */
    public static void sendSmsVerificationCode(String phoneNumber, String code) {
        if (CloudConstants.TXCLOUD.equals(platform)) {
            smsTxVerificationCodeClient(phoneNumber, code);
            return;
        }
        if (CloudConstants.ALIYUN.equals(platform)) {
            smsAliVerificationCodeClient(phoneNumber, code);
        }
    }

    /**
     * 发送短信(自定义)
     *
     * @param smsSendBody 短信发送请求体
     */
    public static void sendSms(SmsSendBody smsSendBody) {
        String platform1 = StringUtils.isEmpty(smsSendBody.getPlatform()) ? smsSendBody.getPlatform() : platform;
        if (CloudConstants.TXCLOUD.equals(platform1)) {
            sendTxSms(smsSendBody);
            return;
        }
        if (CloudConstants.ALIYUN.equals(platform1)) {
            sendAliSms(smsSendBody);
        }
    }

    /**
     * 上传文件
     *
     * @param fileInputStream 文件
     * @param filePath        文件路径(文件存储位置例：filePath/a.jpg)
     * @return 上传文件地址
     */
    public static String upload(InputStream fileInputStream, String filePath) {
        if (CloudConstants.TXCLOUD.equals(platform)) {
            TxUploadFile(fileInputStream, filePath);
            return CloudConstants.getTxCosUrl(bucketName, bucketRegion).concat(filePath);
        }
        if (CloudConstants.ALIYUN.equals(platform)) {
            AliUploadFile(fileInputStream, filePath);
            return CloudConstants.getAliOssUrl(bucketName, bucketRegion).concat(filePath);
        }
        return "";
    }

    /**
     * 上传文件(自定义平台目前支持 腾讯云 和 阿里云)
     *
     * @param upLoadFile 上传文件对象
     * @return 文件地址
     */
    public static String upload(UpLoadFile upLoadFile) {
        if (CloudConstants.TXCLOUD.equals(upLoadFile.getPlatform())) {
            TxUploadFile(upLoadFile.getFileInputStream(), upLoadFile.getFilePath());
            return CloudConstants.getTxCosUrl(upLoadFile.getBucketName(), upLoadFile.getBucketRegion()).concat(upLoadFile.getFilePath());
        }
        if (CloudConstants.ALIYUN.equals(upLoadFile.getPlatform())) {
            AliUploadFile(upLoadFile.getFileInputStream(), upLoadFile.getFilePath());
            return CloudConstants.getAliOssUrl(upLoadFile.getBucketName(), upLoadFile.getBucketRegion()).concat(upLoadFile.getFilePath());
        }
        return "";
    }

    /**
     * 发送短信客户端(腾讯)
     *
     * @param smsSendBody 短信发送请求体
     */
    private static void sendTxSms(SmsSendBody smsSendBody) {
        Credential cred = new Credential(secretId, secretKey);
        SmsClient smsClient = new SmsClient(cred, region);
        com.tencentcloudapi.sms.v20210111.models.SendSmsRequest req = new com.tencentcloudapi.sms.v20210111.models.SendSmsRequest();
        req.setSmsSdkAppId(smsSendBody.getSdkAppId());
        req.setSignName(smsSendBody.getSignName());
        req.setTemplateId(smsSendBody.getTemplateId());
        req.setTemplateParamSet(smsSendBody.getParams());
        req.setPhoneNumberSet(smsSendBody.getPhoneNumbers());
        try {
            SendSmsResponse response = smsClient.SendSms(req);
            log.info("======================短信发送结果：{}", response.toString());
        } catch (TencentCloudSDKException e) {
            throw new UtilException("短信发送异常：" + e.getMessage());
        }
    }

    /**
     * 发送短信客户端(阿里异步)
     *
     * @param smsSendBody 短信发送请求体
     */
    private static void sendAliSms(SmsSendBody smsSendBody) {
        StaticCredentialProvider provider = StaticCredentialProvider.create(com.aliyun.auth.credentials.Credential.builder()
                .accessKeyId(secretId)
                .accessKeySecret(secretKey)
                .build());
        AsyncClient asyncClient = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration
                                .create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com"))
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(smsSendBody.getPhoneNumber())
                .signName(smsSendBody.getSignName())
                .templateCode(smsSendBody.getTemplateId())
                .templateParam(smsSendBody.getParam())
                .build();

        CompletableFuture<com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse> response = asyncClient.sendSms(sendSmsRequest);
        com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new UtilException("阿里云短信发送错误：" + e.getMessage());
        }
        String json = new Gson().toJson(resp);
        asyncClient.close();
        JSONObject jsonObject = JSON.parseObject(json);
        String status = jsonObject.getString("code");
        log.info("=================短信发送成功：{}", status + "=================");

    }

    /**
     * 发送验证码短信(只发送验证码短信)
     */
    private static void smsTxVerificationCodeClient(String phoneNumber, String code) {
        try {
            Credential cred = new Credential(secretId, secretKey);
            SendSmsResponse response = getSendSmsResponse(phoneNumber, code, cred);
            String status = response.getSendStatusSet()[0].getCode();
            log.info("=================短信发送成功：{}=================", status);

        } catch (TencentCloudSDKException e) {
            throw new UtilException("腾讯短信发送错误：" + e.getMessage());
        }
    }

    /**
     * 发送验证码短信(只发送验证码短信)
     */
    private static void smsAliVerificationCodeClient(String phoneNumber, String code) {
        JSONObject params = new JSONObject();
        params.put("code", code);
        params.put("expireTime", expireTime);
        StaticCredentialProvider provider = StaticCredentialProvider.create(com.aliyun.auth.credentials.Credential.builder()
                .accessKeyId(secretId)
                .accessKeySecret(secretKey)
                .build());

        AsyncClient asyncClient = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com"))
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phoneNumber)
                .signName(signName)
                .templateCode(templateId)
                .templateParam(JSONObject.toJSONString(params))
                .build();

        CompletableFuture<com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse> response = asyncClient.sendSms(sendSmsRequest);
        com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new UtilException("阿里云短信发送错误：" + e.getMessage());
        }
        String json = new Gson().toJson(resp);
        asyncClient.close();
        // 发送结果
        String body = JSON.parseObject(json).getString("body");
        String message = JSON.parseObject(body).getString("message");
        log.info("=================短信发送结果：{}=================", message);
    }

    /**
     * 发送验证码短信获取回调(腾讯云)
     *
     * @param phoneNumber 手机号
     * @param code        验证码
     * @param cred        接口票据
     * @return 发送结果
     * @throws TencentCloudSDKException 发送失败异常
     */
    private static SendSmsResponse getSendSmsResponse(String phoneNumber, String code, Credential cred) throws TencentCloudSDKException {
        SmsClient client = new SmsClient(cred, region);
        com.tencentcloudapi.sms.v20210111.models.SendSmsRequest req = new com.tencentcloudapi.sms.v20210111.models.SendSmsRequest();
        req.setSmsSdkAppId(sdkAppId);
        req.setSignName(signName);
        req.setTemplateId(templateId);
        String[] phoneNumbers = {phoneNumber};
        req.setPhoneNumberSet(phoneNumbers);
        String[] templateParams = {code, expireTime};
        req.setTemplateParamSet(templateParams);
        return client.SendSms(req);
    }

    /**
     * 创建存储桶连接客户端实例（腾讯云）
     */
    private static COSClient createCOSClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(bucketRegion));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 设置 socket 读取超时，默认 30s
        clientConfig.setSocketTimeout(30 * 1000);
        // 设置建立连接超时，默认 30s
        clientConfig.setConnectionTimeout(30 * 1000);

        return new COSClient(cred, clientConfig);
    }

    /**
     * 创建 TransferManager 实例，这个实例用来后续调用高级接口（腾讯云存储）
     */
    private static TransferManager createTransferManager() {
        COSClient cosClient = createCOSClient();

        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(16);

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(2 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    /**
     * 关闭TransferManager管理器
     */
    static void shutdownTransferManager(TransferManager transferManager) {
        // 指定参数为 true, 则同时会关闭 transferManager 内部的 COSClient 实例。
        // 指定参数为 false, 则不会关闭 transferManager 内部的 COSClient 实例。
        transferManager.shutdownNow(true);
    }

    /**
     * 上传文件（腾讯云）
     */
    private static void TxUploadFile(InputStream fileInputStream, String filePath) {
        TransferManager transferManager = createTransferManager();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filePath, fileInputStream, null);
        // 设置存储类型：默认是标准(Standard), 低频(standard_ia)
        putObjectRequest.setStorageClass(StorageClass.Standard);
        try {
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
            log.info("腾讯云上传结果：{}", uploadResult);
        } catch (CosClientException | InterruptedException e) {
            throw new ServiceException("腾讯云服务上传文件失败：" + e.getMessage());
        }
        shutdownTransferManager(transferManager);

    }

    /**
     * 文件上传（阿里云）
     */
    private static void AliUploadFile(InputStream fileInputStream, String filePath) {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        DefaultCredentialProvider credentialsProvider = new DefaultCredentialProvider(secretId, secretKey);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(bucketEndpoint, credentialsProvider);
        try {
            // 创建PutObjectRequest对象。
            com.aliyun.oss.model.PutObjectRequest putObjectRequest =
                    new com.aliyun.oss.model.PutObjectRequest(bucketName, filePath, fileInputStream);

            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("阿里云上传结果：{}", result);

        } catch (OSSException oe) {
            log.info("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
            log.info("Error Message:{}", oe.getErrorMessage());
            log.info("Error Code:{}", oe.getErrorCode());
            log.info("Request ID:{}", oe.getRequestId());
            log.info("Host ID:{}", oe.getHostId());
            throw new ServiceException("阿里云文件上传失败" + oe.getMessage());
        } catch (ClientException ce) {
            log.info("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.info("Error Message:{}", ce.getMessage());
            throw new ServiceException("阿里云文件上传失败（客户端连接失败)）" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
