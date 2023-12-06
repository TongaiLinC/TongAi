package com.tawl.system.domain;

import com.tawl.common.annotation.Excel;
import com.tawl.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * 文件管理对象 sys_file_info
 * 
 * @author tongaikeji
 * @date 2023-10-27
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysFileInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private Long fileId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件类别 */
    @Excel(name = "文件类别")
    private String fileType;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private BigDecimal fileSize;

    /** 数据所属人ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 文件 */
    private MultipartFile file;

}
