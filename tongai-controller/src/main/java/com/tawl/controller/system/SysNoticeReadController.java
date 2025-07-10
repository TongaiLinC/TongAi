package com.tawl.controller.system;

import com.tawl.common.annotation.Log;
import com.tawl.common.core.controller.BaseController;
import com.tawl.common.core.domain.AjaxResult;
import com.tawl.common.core.page.TableDataInfo;
import com.tawl.common.enums.BusinessType;
import com.tawl.common.utils.poi.ExcelUtil;
import com.tawl.system.domain.SysNoticeRead;
import com.tawl.system.service.ISysNoticeReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通知公告阅读记录Controller
 * 
 * @author tongaikeji
 * @date 2023-09-03
 */
@RestController
@RequestMapping("/system/read")
public class SysNoticeReadController extends BaseController
{
    @Autowired
    private ISysNoticeReadService sysNoticeReadService;

    /**
     * 查询通知公告阅读记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:read:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysNoticeRead sysNoticeRead)
    {
        startPage();
        List<SysNoticeRead> list = sysNoticeReadService.selectSysNoticeReadList(sysNoticeRead);
        return getDataTable(list);
    }

    /**
     * 导出通知公告阅读记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:read:export')")
    @Log(title = "通知公告阅读记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysNoticeRead sysNoticeRead)
    {
        List<SysNoticeRead> list = sysNoticeReadService.selectSysNoticeReadList(sysNoticeRead);
        ExcelUtil<SysNoticeRead> util = new ExcelUtil<SysNoticeRead>(SysNoticeRead.class);
        util.exportExcel(response, list, "通知公告阅读记录数据");
    }

    /**
     * 获取通知公告阅读记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:read:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysNoticeReadService.selectSysNoticeReadById(id));
    }

    /**
     * 新增通知公告阅读记录
     */
    @PreAuthorize("@ss.hasPermi('system:read:add')")
    @Log(title = "通知公告阅读记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysNoticeRead sysNoticeRead)
    {
        return toAjax(sysNoticeReadService.insertSysNoticeRead(sysNoticeRead));
    }

    /**
     * 修改通知公告阅读记录
     */
    @PreAuthorize("@ss.hasPermi('system:read:edit')")
    @Log(title = "通知公告阅读记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysNoticeRead sysNoticeRead)
    {
        return toAjax(sysNoticeReadService.updateSysNoticeRead(sysNoticeRead));
    }

    /**
     * 删除通知公告阅读记录
     */
    @PreAuthorize("@ss.hasPermi('system:read:remove')")
    @Log(title = "通知公告阅读记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysNoticeReadService.deleteSysNoticeReadByIds(ids));
    }

    /**
     * 获取通知公告阅读未读条数
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
	@GetMapping("/getUnReadCount")
    public int getUnReadCount()
    {
        return sysNoticeReadService.getUnReadCount();
    }
}
