package com.tawl.generator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 * 
 * @author tongai
 */
@Component
@ConfigurationProperties(prefix = "gen")
@PropertySource(value = { "classpath:generator.yml" })
public class GenConfig
{
    /** 作者 */
    public static String author;

    /** 生成包路径 */
    public static String packageName;

    /** 自动去除表前缀，默认是false */
    public static boolean autoRemovePre;

    /** 表前缀(类名不会包含表前缀) */
    public static String tablePrefix;

    /** 数据库链接地址 */
    public static String url;

    /** 数据库连接用户名 */
    public static String username;

    /** 数据库用户密码 */
    public static String password;

    public static String getAuthor()
    {
        return author;
    }

    @Value("${author}")
    public void setAuthor(String author)
    {
        GenConfig.author = author;
    }

    public static String getPackageName()
    {
        return packageName;
    }

    @Value("${packageName}")
    public void setPackageName(String packageName)
    {
        GenConfig.packageName = packageName;
    }

    public static boolean getAutoRemovePre()
    {
        return autoRemovePre;
    }

    @Value("${autoRemovePre}")
    public void setAutoRemovePre(boolean autoRemovePre)
    {
        GenConfig.autoRemovePre = autoRemovePre;
    }

    public static String getTablePrefix()
    {
        return tablePrefix;
    }

    @Value("${tablePrefix}")
    public void setTablePrefix(String tablePrefix)
    {
        GenConfig.tablePrefix = tablePrefix;
    }


    public static String getUrl() {
        return url;
    }

    @Value("${url}")
    public void setUrl(String url) {
        this.url = url;
    }

    public static String getUsername() {
        return username;
    }

    @Value("${username}")
    public void setUsername(String username) {
        this.username = username;
    }

    public static String getPassword() {
        return password;
    }

    @Value("${password}")
    public void setPassword(String password) {
        this.password = password;
    }
}
