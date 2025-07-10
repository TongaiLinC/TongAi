package com.tawl.controller.system;

import com.tawl.common.annotation.Log;
import com.tawl.common.core.controller.BaseController;
import com.tawl.common.core.domain.AjaxResult;
import com.tawl.common.core.page.TableDataInfo;
import com.tawl.common.enums.BusinessType;
import com.tawl.system.domain.SysNotice;
import com.tawl.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告 信息操作处理
 * 
 * @author tongai
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController
{
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表(发布者列表)
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 获取通知公告列表(用户列表)
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/readList")
    public TableDataInfo readList(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectReadNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 获取通知公告列表(未读列表)
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/unReadList")
    public TableDataInfo unReadList(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectUnReadList(notice);
        return getDataTable(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId)
    {
        return success(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysNotice notice)
    {
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysNotice notice)
    {
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds)
    {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }

    /**
     * 根据通知公告编号修改阅读状态
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping(value = "/changeReadStatus/{noticeId}")
    public AjaxResult changeReadStatus(@PathVariable Long noticeId)
    {
        noticeService.changeReadStatus(noticeId);
        return success();
    }

    /**
     * 设置为已读
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @Log(title = "通知公告", businessType = BusinessType.OTHER)
    @GetMapping("/setAllNoticeRead/{noticeIds}")
    public AjaxResult setAllNoticeRead(@PathVariable Long[] noticeIds)
    {
        noticeService.setAllNoticeRead(noticeIds);
        return success();
    }
}
