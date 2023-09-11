package com.tawl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author tongai
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class TongAiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(TongAiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  同乂启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-') _      ('-.     \n" +
                "(  OO) )    ( OO ).-. \n" +
                "/     '._   / . --. / \n" +
                "|'--...__)  | \\-.  \\  \n" +
                "'--.  .--'.-'-'  |  | \n" +
                "   |  |    \\| |_.'  | \n" +
                "   |  |     |  .-.  | \n" +
                "   |  |     |  | |  | \n" +
                "   `--'     `--' `--' \n");
    }
}
