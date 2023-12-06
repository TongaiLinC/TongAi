package com.tawl.controller.system;

import com.tawl.common.annotation.Log;
import com.tawl.common.core.controller.BaseController;
import com.tawl.common.core.domain.AjaxResult;
import com.tawl.common.core.page.TableDataInfo;
import com.tawl.common.enums.BusinessType;import com.tawl.system.domain.SysFileInfo;
import com.tawl.system.service.ISysFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件管理Controller
 * 
 * @author tongaikeji
 * @date 2023-10-27
 */
@RestController
@RequestMapping("/system/fileinfo")
public class SysFileInfoController extends BaseController
{
    @Autowired
    private ISysFileInfoService sysFileInfoService;

    /**
     * 查询文件管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:fileinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFileInfo sysFileInfo)
    {
        startPage();
        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
        return getDataTable(list);
    }

    /**
     * 获取文件管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:fileinfo:query')")
    @GetMapping(value = "/{fileId}")
    public AjaxResult getInfo(@PathVariable("fileId") Long fileId)
    {
        return success(sysFileInfoService.selectSysFileInfoByFileId(fileId));
    }

    /**
     * 新增文件管理
     */
    @PreAuthorize("@ss.hasPermi('system:fileinfo:add')")
    @Log(title = "文件管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysFileInfo sysFileInfo)
    {
        return toAjax(sysFileInfoService.insertSysFileInfo(sysFileInfo));
    }

    /**
     * 修改文件管理
     */
    @PreAuthorize("@ss.hasPermi('system:fileinfo:edit')")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFileInfo sysFileInfo)
    {
        return toAjax(sysFileInfoService.updateSysFileInfo(sysFileInfo));
    }

    /**
     * 删除文件管理
     */
    @PreAuthorize("@ss.hasPermi('system:fileinfo:remove')")
    @Log(title = "文件管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable Long[] fileIds)
    {
        return toAjax(sysFileInfoService.deleteSysFileInfoByFileIds(fileIds));
    }

}
