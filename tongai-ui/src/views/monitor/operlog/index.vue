<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="操作地址" prop="operIp">
        <el-input
            v-model="queryParams.operIp"
            placeholder="请输入操作地址"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系统模块" prop="title">
        <el-input
            v-model="queryParams.title"
            placeholder="请输入系统模块"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="operName">
        <el-input
            v-model="queryParams.operName"
            placeholder="请输入操作人员"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="businessType">
        <el-select
            v-model="queryParams.businessType"
            placeholder="操作类型"
            clearable
            style="width: 240px"
        >
          <el-option
              v-for="dict in dict.type.sys_oper_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
            v-model="queryParams.status"
            placeholder="操作状态"
            clearable
            style="width: 240px"
        >
          <el-option
              v-for="dict in dict.type.sys_common_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间">
        <el-date-picker
            v-model="dateRange"
            style="width: 240px"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['monitor:operlog:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            @click="handleClean"
            v-hasPermi="['monitor:operlog:remove']"
        >清空
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['monitor:operlog:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange"
              :default-sort="defaultSort" @sort-change="handleSortChange"
    >
      <el-table-column type="selection" width="50" align="center"/>
      <el-table-column label="日志编号" align="center" prop="operId"/>
      <el-table-column label="系统模块" align="center" prop="title" :show-overflow-tooltip="true"/>
      <el-table-column label="操作类型" align="center" prop="businessType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_oper_type" :value="scope.row.businessType"/>
        </template>
      </el-table-column>
      <el-table-column label="操作人员" align="center" prop="operName" width="110" :show-overflow-tooltip="true"
                       sortable="custom" :sort-orders="['descending', 'ascending']"
      />
      <el-table-column label="操作地址" align="center" prop="operIp" width="130" :show-overflow-tooltip="true"/>
      <el-table-column label="操作地点" align="center" prop="operLocation" :show-overflow-tooltip="true"/>
      <el-table-column label="操作状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_common_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作日期" align="center" prop="operTime" width="160" sortable="custom"
                       :sort-orders="['descending', 'ascending']"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.operTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="消耗时间" align="center" prop="costTime" width="110" :show-overflow-tooltip="true"
                       sortable="custom" :sort-orders="['descending', 'ascending']"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.costTime }}毫秒</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row,scope.index)"
              v-hasPermi="['monitor:operlog:query']"
          >详细
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 操作日志详细 -->
    <el-drawer title="操作日志详细" :visible.sync="open">
      <div class="drawer__content">
        <el-descriptions direction="vertical" :column="1" border>
          <el-descriptions-item label="操作模块：">{{ form.title }} / {{ typeFormat(form) }}</el-descriptions-item>
          <el-descriptions-item label="登录信息：">{{ form.operName }} / {{ form.operIp }} / {{
              form.operLocation
            }}
          </el-descriptions-item>
          <el-descriptions-item label="请求地址：">{{ form.operUrl }}</el-descriptions-item>
          <el-descriptions-item label="请求方式：">{{ form.requestMethod }}</el-descriptions-item>
          <el-descriptions-item label="操作方法：">{{ form.method }}</el-descriptions-item>
          <el-descriptions-item label="日志信息：">{{ form.jobMessage }}</el-descriptions-item>
          <el-descriptions-item label="请求参数：">
            <div class="jsonCode">
              <codemirror :style="{ height: '100%' }" :options="options" v-model="form.operParam"></codemirror>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="返回参数：">
            <div class="jsonCode">
              <codemirror :style="{ height: '100%' }" :options="options" v-model="form.jsonResult"></codemirror>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="操作状态：">
            <el-tag type="success" v-if="form.status === 0">正常</el-tag>
            <el-tag type="danger" v-else-if="form.status === 1">失败</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="消耗时间：">{{ form.costTime }}毫秒</el-descriptions-item>
          <el-descriptions-item label="操作时间：">{{ parseTime(form.operTime) }}</el-descriptions-item>
          <el-descriptions-item label="异常信息：" v-if="form.status === 1">
            {{ form.errorMsg }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { cleanOperlog, delOperlog, list } from '@/api/monitor/operlog'
// 代码高亮显示
import hljs from 'highlight.js/lib/highlight'
import 'highlight.js/styles/github-gist.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/javascript/javascript'
// 主题
import 'codemirror/theme/zenburn.css'

import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/json-lint'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/addon/fold/brace-fold.js'
import 'codemirror/addon/fold/xml-fold.js'
import 'codemirror/addon/fold/indent-fold.js'
import 'codemirror/addon/fold/markdown-fold.js'

// 折叠
import 'codemirror/addon/fold/foldgutter.css'
import 'codemirror/addon/fold/foldcode'
import 'codemirror/addon/fold/foldgutter'
import 'codemirror/addon/fold/brace-fold'
import 'codemirror/addon/fold/comment-fold'

hljs.registerLanguage('json', require('highlight.js/lib/languages/json'))

export default {
  name: 'Operlog',
  dicts: ['sys_oper_type', 'sys_common_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 默认排序
      defaultSort: { prop: 'operTime', order: 'descending' },
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        operIp: undefined,
        title: undefined,
        operName: undefined,
        businessType: undefined,
        status: undefined
      },
      options: {
        tabSize: 4, // 缩进格式
        theme: 'zenburn', // 指定主题，对应主题库 JS 需要提前引入
        lineNumbers: false, // 是否显示行号
        //指定语言类型,如果需要编辑和显示其他语言,需要import语言js然后修改此配置
        mode: 'application/json',
        line: true,
        styleActiveLine: true, // 高亮选中行
        //是否为只读,如果为"nocursor" 不仅仅为只读 连光标都无法在区域聚焦
        readOnly: true,
        hintOptions: {
          completeSingle: true // 当匹配只有一项的时候是否自动补全
        },
        gutters: []
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询登录日志 */
    getList() {
      this.loading = true
      list(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
            this.list = response.rows
            this.total = response.total
            this.loading = false
          }
      )
    },
    // 操作日志类型字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.dict.type.sys_oper_type, row.businessType)
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order)
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.operId)
      this.multiple = !selection.length
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop
      this.queryParams.isAsc = column.order
      this.getList()
    },
    /** 详细按钮操作 */
    handleView(row) {
      this.open = true
      this.form = row
      let operParamJson = JSON.parse(row.operParam)
      let resultJson = JSON.parse(row.jsonResult)
      this.form.operParam = JSON.stringify(operParamJson, null, 4)
      this.form.jsonResult = JSON.stringify(resultJson, null, 4)
      this.$refs.VueCodemirror.setSize('100%', 'auto')
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const operIds = row.operId || this.ids
      this.$modal.confirm('是否确认删除日志编号为"' + operIds + '"的数据项？').then(function() {
        return delOperlog(operIds)
      }).then(() => {
        this.getList()
        this.$modal.notifySuccess('删除成功')
      }).catch(() => {
      })
    },
    /** 清空按钮操作 */
    handleClean() {
      this.$modal.confirm('是否确认清空所有操作日志数据项？').then(function() {
        return cleanOperlog()
      }).then(() => {
        this.getList()
        this.$modal.notifySuccess('清空成功')
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('monitor/operlog/export', {
        ...this.queryParams
      }, `operlog_${new Date().getTime()}.xlsx`)
    },
    /** 高亮显示 */
    highlightedCode(content) {
      const result = hljs.highlight('json', content || '', true)
      return result.value || '&nbsp;'
    }
  }
}
</script>
