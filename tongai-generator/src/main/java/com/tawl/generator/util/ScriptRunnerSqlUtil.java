package com.tawl.generator.util;

import com.tawl.common.exception.ServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 运行SQL文件工具类
 *
 * @author tongai
 * @version V1.0.0
 * @date 2023-08-17
 */
@Slf4j
@Data
public class ScriptRunnerSqlUtil {

    private String url;
    private String username;
    private String password;

    /**
     * 获取当前Mybatis的连接信息
     *
     * @return Connection
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            // 建立连接
            conn = DriverManager.getConnection(this.url, this.username, this.password);
            log.info("数据库连接是否关闭？：{}", conn.isClosed());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return conn;
    }


    /**
     * 运行sql脚本
     *
     * @param sqlFilePath 执行的脚本文件
     */
    public void runSql(String sqlFilePath) {
        try {
            // 建立连接
            Connection conn = this.getConnection();
            // 创建ScriptRunner，用于执行SQL脚本
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);
            // 遇到错误回滚
            runner.setStopOnError(true);
            Resources.setCharset(StandardCharsets.UTF_8);
            // 执行SQL脚本
            runner.runScript(new InputStreamReader(Files.newInputStream(Paths.get(sqlFilePath)), StandardCharsets.UTF_8));
            // 关闭连接
            conn.close();
        } catch (Exception e) {
            log.error("【MySQL】->执行SQL脚本失败：{}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }
}