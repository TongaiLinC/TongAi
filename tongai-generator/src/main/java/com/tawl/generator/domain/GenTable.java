package com.tawl.generator.domain;

import com.tawl.common.constant.GenConstants;
import com.tawl.common.core.domain.BaseEntity;
import com.tawl.common.utils.StringUtils;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 业务表 gen_table
 * 
 * @author tongai
 */
@Getter
public class GenTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long tableId;

    /** 表名称 */
    @NotBlank(message = "表名称不能为空")
    private String tableName;

    /** 表描述 */
    @NotBlank(message = "表描述不能为空")
    private String tableComment;

    /** 关联父表的表名 */
    private String subTableName;

    /** 本表关联父表的外键名 */
    private String subTableFkName;

    /** 实体类名称(首字母大写) */
    @NotBlank(message = "实体类名称不能为空")
    private String className;

    /** 使用的模板（crud单表操作 tree树表操作 sub主子表操作） */
    private String tplCategory;

    /** 生成包路径 */
    @NotBlank(message = "生成包路径不能为空")
    private String packageName;

    /** 生成模块名 */
    @NotBlank(message = "生成模块名不能为空")
    private String moduleName;

    /** 生成业务名 */
    @NotBlank(message = "生成业务名不能为空")
    private String businessName;

    /** 生成功能名 */
    @NotBlank(message = "生成功能名不能为空")
    private String functionName;

    /** 生成作者 */
    @NotBlank(message = "作者不能为空")
    private String functionAuthor;

    /** 生成代码方式（0zip压缩包 1自定义路径） */
    private String genType;

    /** 生成路径（不填默认项目路径） */
    private String genPath;

    /** 主键信息 */
    private GenTableColumn pkColumn;

    /** 子表信息 */
    private GenTable subTable;

    /** 表列信息 */
    @Valid
    private List<GenTableColumn> columns;

    /** 其它生成选项 */
    private String options;

    /** 树编码字段 */
    private String treeCode;

    /** 树父编码字段 */
    private String treeParentCode;

    /** 树名称字段 */
    private String treeName;

    /** 上级菜单ID字段 */
    private Long parentMenuId;

    /** 上级菜单名称字段 */
    private String parentMenuName;

    /** 数据权限注解 0-不添加注解，1-添加注解 */
    private String dataAuth;

    /** 前端模板 0-仅生成API，1-生成API和界面 */
    private String frontTemplate;

    /** 逻辑删除 0-否，1-是 */
    private String tombstones;

    /** 导入导出 0-否，1-仅导出 2-仅导入 3-导入和导出*/
    private String isImportAndExport;

    public void setIsImportAndExport(String isImportAndExport) {
        this.isImportAndExport = isImportAndExport;
    }

    public void setTombstones(String tombstones) {
        this.tombstones = tombstones;
    }
    public void setFrontTemplate(String frontTemplate) {
        this.frontTemplate = frontTemplate;
    }

    public void setDataAuth(String dataAuth) {
        this.dataAuth = dataAuth;
    }

    public void setTableId(Long tableId)
    {
        this.tableId = tableId;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public void setTableComment(String tableComment)
    {
        this.tableComment = tableComment;
    }

    public void setSubTableName(String subTableName)
    {
        this.subTableName = subTableName;
    }

    public void setSubTableFkName(String subTableFkName)
    {
        this.subTableFkName = subTableFkName;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public void setTplCategory(String tplCategory)
    {
        this.tplCategory = tplCategory;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public void setBusinessName(String businessName)
    {
        this.businessName = businessName;
    }

    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
    }

    public void setFunctionAuthor(String functionAuthor)
    {
        this.functionAuthor = functionAuthor;
    }

    public void setGenType(String genType)
    {
        this.genType = genType;
    }

    public void setGenPath(String genPath)
    {
        this.genPath = genPath;
    }

    public void setPkColumn(GenTableColumn pkColumn)
    {
        this.pkColumn = pkColumn;
    }

    public void setSubTable(GenTable subTable)
    {
        this.subTable = subTable;
    }

    public void setColumns(List<GenTableColumn> columns)
    {
        this.columns = columns;
    }

    public void setOptions(String options)
    {
        this.options = options;
    }

    public void setTreeCode(String treeCode)
    {
        this.treeCode = treeCode;
    }

    public void setTreeParentCode(String treeParentCode)
    {
        this.treeParentCode = treeParentCode;
    }

    public void setTreeName(String treeName)
    {
        this.treeName = treeName;
    }

    public void setParentMenuId(Long parentMenuId)
    {
        this.parentMenuId = parentMenuId;
    }

    public void setParentMenuName(String parentMenuName)
    {
        this.parentMenuName = parentMenuName;
    }

    public boolean isSub()
    {
        return isSub(this.tplCategory);
    }

    public static boolean isSub(String tplCategory)
    {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_SUB, tplCategory);
    }

    public boolean isTree()
    {
        return isTree(this.tplCategory);
    }

    public static boolean isTree(String tplCategory)
    {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_TREE, tplCategory);
    }

    public boolean isCrud()
    {
        return isCrud(this.tplCategory);
    }

    public static boolean isCrud(String tplCategory)
    {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_CRUD, tplCategory);
    }

    public boolean isSuperColumn(String javaField)
    {
        return isSuperColumn(this.tplCategory, javaField);
    }

    public static boolean isSuperColumn(String tplCategory, String javaField)
    {
        if (isTree(tplCategory))
        {
            return StringUtils.equalsAnyIgnoreCase(javaField,
                    ArrayUtils.addAll(GenConstants.TREE_ENTITY, GenConstants.BASE_ENTITY));
        }
        return StringUtils.equalsAnyIgnoreCase(javaField, GenConstants.BASE_ENTITY);
    }
}