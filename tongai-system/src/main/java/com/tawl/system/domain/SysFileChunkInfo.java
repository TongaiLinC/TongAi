package com.tawl.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tawl.common.annotation.Excel;
import com.tawl.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * 文件分片信息对象 sys_file_chunk_info
 * 
 * @author tongaikeji
 * @date 2025-04-05
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysFileChunkInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分片ID */
    private Long id;

    /** 分片编号 */
    @Excel(name = "分片编号")
    private Long chunkNumber;

    /** 分片大小 */
    @Excel(name = "分片大小")
    private Long chunkSize;

    /** 文件唯一标识 */
    @Excel(name = "文件唯一标识")
    private String identifier;

    /** 分片存储相对路径 */
    @Excel(name = "分片存储相对路径")
    private String relativePath;

    /** 总分片数 */
    @Excel(name = "总分片数")
    private Long totalChunks;

    /** 数据所属人ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 文件 */
    @TableField(exist = false)
    private MultipartFile file;

    /** 文件名称 */
    @TableField(exist = false)
    private String fileName;

    /** 文件大小 */
    @TableField(exist = false)
    private BigDecimal fileSize;

}