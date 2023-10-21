package com.tawl.quartz.task;

import com.tawl.common.utils.StringUtils;
import com.tawl.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author tongaikeji
 * @date 2023-08-18
 */
@Component("taTask")
public class TaTask {

    @Autowired
    private ISysOperLogService sysOperLogService;

    /**
     * 通过接口清除所有用户日志
     */
    public void clearAllLog(Integer day) {
        sysOperLogService.cleanOperLog(day);
        System.out.println("=========定时任务执行了,日志清除成功==========");
        System.out.println(StringUtils.format("============当前日志保留天数：{}天============", day));
        System.out.println("==========================================");
    }

}
