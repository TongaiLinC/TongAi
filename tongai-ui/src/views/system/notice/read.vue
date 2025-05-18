<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布人" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入发布人名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="公告类型" @change="handleQuery" clearable>
          <el-option
            v-for="dict in dict.type.sys_notice_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          size="mini"
          @click="handleUpdateAllRead"
        >标记为已读
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          size="mini"
          @click="getReadList"
        >所有消息
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          size="mini"
          @click="getUnReadList"
        >未读消息
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getReadList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" prop="noticeId" width="100"/>
      <el-table-column
        label="公告标题"
        align="center"
        prop="noticeTitle"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="公告类型" align="center" prop="noticeType" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_type" :value="scope.row.noticeType"/>
        </template>
      </el-table-column>
      <el-table-column label="重要等级" align="center" prop="weight" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_weight" :value="scope.row.weight"/>
        </template>
      </el-table-column>
      <el-table-column label="是否已读" align="center" prop="isRead" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_read" :value="scope.row.isRead"/>
        </template>
      </el-table-column>
      <el-table-column label="发布者" align="center" prop="createBy" width="150"/>
      <el-table-column label="发布时间" align="center" prop="publishTime" width="200">
        <template slot-scope="scope">
          <span>{{ formatDateTime(scope.row.publishTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermi="['system:notice:preview']"
          >查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getReadList"
    />

    <!-- 查看通知详情 -->
    <el-dialog
      center
      width="35vw"
      :title="form.noticeTitle"
      :visible.sync="centerDialogVisible"
      :close-on-click-modal="false"
      append-to-body
    >
      <div class="dialog-content">
        <div class="dialog-content-header">
          <el-row :span="24" style="display: flex; align-items: center">
            <el-col class="col-inline" :span="16">
              消息类别：
              <dict-tag :options="dict.type.sys_notice_type" :value="form.noticeType"/>
            </el-col>
            <el-col class="col-inline" :span="5">
              重要等级：
              <dict-tag :options="dict.type.sys_notice_weight" :value="form.weight"/>
            </el-col>
          </el-row>
          <el-row :span="24">
            <el-col class="col-inline" :span="16">
              发布时间：<span>{{ formatDateTime(form.publishTime) }}</span>
            </el-col>
            <el-col class="col-inline" :span="5">
              发布人员：<span>{{ form.createBy }}</span>
            </el-col>
          </el-row>
        </div>
        <el-divider></el-divider>
        <div class="dialog-content-body">
          <div class="ql-container ql-bubble">
            <div class="ql-editor">
              <div style="max-height: 65vh;min-height: 400px" v-html="form.noticeContent"></div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {changeReadStatus, getUnReadCount, listReadNotice, listUnReadNotice, setAllNoticeRead} from '@/api/system/notice'
import {formatTime} from '@/utils'

export default {
  name: 'NoticeRead',
  dicts: ['sys_notice_type', 'sys_notice_read', 'sys_notice_weight'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      noticeList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示通知详情弹窗
      centerDialogVisible: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: undefined,
        createBy: undefined,
        status: undefined,
        readStatus: false
      },
      // 表单参数
      form: {}
    }
  },
  created() {
    this.getReadList()
    this.handleGetParams()
  },
  methods: {
    // 时间格式化，几天前，几小时前
    formatDateTime(time) {
      if (time) {
        return formatTime(Date.parse(new Date(time).toString()), '')
      }
      return ''
    },
    /** 获取传递的参数，如果存在参数则直接打开详情页 */
    handleGetParams() {
      setTimeout(() => {
        let noticeId = Number(this.$route.params.id)
        if (noticeId) {
          this.noticeList.forEach(item => {
            if (item.noticeId === noticeId) {
              this.handlePreview(item)
            }
          })
        }
      }, 500)
    },
    /** 查询公告列表 */
    getReadList() {
      this.loading = true
      this.queryParams.readStatus = false
      listReadNotice(this.queryParams).then(response => {
        this.noticeList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 查询未读公告列表 */
    getUnReadList() {
      this.loading = true
      this.queryParams.readStatus = true
      listUnReadNotice(this.queryParams).then(response => {
        this.noticeList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      if (this.queryParams.readStatus) {
        this.getUnReadList()
        return
      }
      this.getReadList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.readStatus = false
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.noticeId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 查看按钮操作 */
    handlePreview(row) {
      this.form = row
      this.refreshUnReadStatus()
      this.centerDialogVisible = true
    },
    /** 获取未读通知个数阅读状态，通过store进行状态管理 */
    getUnReadCount() {
      getUnReadCount().then((res) => {
        this.$store.commit('SET_UNREAD_COUNT', res)
      })
    },
    /** 刷新列表和头像阅读状态和角标 */
    async refreshUnReadStatus() {
      if (this.form.isRead === '0') {
        // 设置为已读
        changeReadStatus(this.form.noticeId)
        // 打开后0.8S后更新列表和未读通知数
        await setTimeout(() => {
          this.getReadList()
          this.getUnReadCount()
        }, 800)
      }
    },
    /** 标记为已读 */
    handleUpdateAllRead() {
      if (this.ids.length === 0) {
        this.ids = this.noticeList.map((item) => {
          if (item.isRead === '0') {
            return item.noticeId
          }
        }).filter(id => id !== undefined).join(',')
      }
      if (this.ids.length > 0) {
        setAllNoticeRead(this.ids).then(response => {
          this.$notify.success('设置成功')
          this.getReadList()
          this.getUnReadCount()
        })
      }

    }
  }
}
</script>
<style lang="scss" scoped>
.el-divider {
  margin-top: 10px
}

.dialog-content {
  margin-top: -25px;
}

.col-inline {
  display: inline-flex;
  align-items: center;
}
</style>
