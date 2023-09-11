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
      <el-form-item label="操作人员" prop="createBy">
        <el-input
            v-model="queryParams.createBy"
            placeholder="请输入操作人员"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="公告类型" @change="handleQuery"  clearable>
          <el-option
              v-for="dict in dict.type.sys_notice_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="公告状态" @change="handleQuery"  clearable>
          <el-option
              v-for="dict in dict.type.sys_notice_status"
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
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['system:notice:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['system:notice:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:notice:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
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
      <el-table-column label="状态" align="center" prop="status" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="重要等级" align="center" prop="weight" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_weight" :value="scope.row.weight"/>
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" label="接收人员" align="center" prop="userIds" width="250">
        <template slot-scope="scope">
          <span v-if="scope.row.userIds === '0'">所有用户</span>
          <span v-else>{{ getUserByIds(scope.row.userIds) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="150"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="200" sortable>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="publishTime" width="200" sortable>
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
              v-hasPermi="['system:notice:edit']"
          >预览
          </el-button>
          <el-button
              v-if="scope.row.status !== '1'"
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:notice:edit']"
          >修改
          </el-button>
          <el-button
              v-if="scope.row.status !== '1'"
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handlePublish(scope.row)"
              v-hasPermi="['system:notice:edit']"
          >发布
          </el-button>
          <el-button
              style="color: #ff0000;"
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:notice:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0"
                :total="total"
                :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize"
                @pagination="getList"
    />

    <!-- 添加或修改公告对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择公告类型">
                <el-option v-for="dict in dict.type.sys_notice_type" :key="dict.value" :label="dict.label"
                           :value="dict.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="重要等级" prop="weight">
              <el-select v-model="form.weight" placeholder="请选择重要等级">
                <el-option v-for="dict in dict.type.sys_notice_weight" :key="dict.value" :label="dict.label"
                           :value="dict.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知人员">
              <el-select v-model="form.userIds" multiple filterable clearable collapse-tags
                         placeholder="请选择通知对象"
              >
                <el-option v-for="user in userList" :key="user.userId" :label="user.nickName" :value="user.userId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容">
              <editor v-model="form.noticeContent" :min-height="200" :height="400"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">仅保存</el-button>
        <el-button type="primary" @click="submitForm('publish')">保存并发布</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看通知详情 -->
    <el-dialog
        center
        width="25vw"
        :title="form.noticeTitle"
        :visible.sync="centerDialogVisible"
        :close-on-click-modal="false"
        append-to-body
    >
      <div class="dialog-content">
        <div class="dialog-content-header">
          <el-row style="display: flex; align-items: center">
            <el-col class="col-inline" :span="12">
              消息类别：
              <dict-tag :options="dict.type.sys_notice_type" :value="form.noticeType"/>
            </el-col>
            <el-col class="col-inline" :span="12">
              重要等级：
              <dict-tag :options="dict.type.sys_notice_weight" :value="form.weight"/>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="col-inline" :span="12">
              <span>发布时间：{{ formatDateTime(form.publishTime) }}</span>
            </el-col>
            <el-col class="col-inline" style="float: right" :span="12">
              <span>发布人员：{{ form.createBy }}</span>
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
import {addNotice, delNotice, getNotice, listNotice, updateNotice} from '@/api/system/notice'
import {userList} from '@/api/system/user'
import {formatDate, formatTime} from '@/utils'

export default {
  name: 'Notice',
  dicts: ['sys_notice_status', 'sys_notice_type', 'sys_notice_weight'],
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
        status: undefined
      },
      // 查询参数
      queryUserParams: {
        pageNum: 1,
        pageSize: 30,
        userName: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeTitle: [
          { required: true, message: '公告标题不能为空', trigger: 'blur' }
        ],
        noticeType: [
          { required: true, message: '公告类型不能为空', trigger: 'change' }
        ]
      },
      userList: []
    }
  },
  created() {
    this.getList()
    this.getUserList()
  },
  methods: {
    /** 查询公告列表 */
    getList() {
      this.loading = true
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 时间格式化，几天前，几小时前
    formatDateTime(time){
      if (time) {
        return formatTime(Date.parse(new Date(time).toString()), '')
      }
      return ''
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        noticeId: undefined,
        noticeTitle: undefined,
        noticeType: undefined,
        noticeContent: undefined,
        status: '0',
        userIds: ''
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.noticeId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加公告'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const noticeId = row.noticeId || this.ids
      getNotice(noticeId).then(response => {
        this.form = response.data
        this.form.userIds = response.data.userIds.split(',').map(item => Number(item))
        this.open = true
        this.title = '修改公告'
      })
    },
    /** 发布按钮操作 */
    handlePublish(row) {
      row.status = '1'
      row.publishTime = formatDate(new Date())
      updateNotice(row).then(response => {
        this.$modal.notifySuccess('发布成功')
        this.getList()
      })
    },
    /** 提交按钮 */
    submitForm: function(type) {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.form.userIds = this.form.userIds.join(',')
          this.form.status = '0'
          if (type === 'publish') {
            this.form.status = '1'
            this.form.publishTime = formatDate(new Date())
          }
          if (this.form.noticeId !== undefined) {
            updateNotice(this.form).then(response => {
              this.$modal.notifySuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addNotice(this.form).then(response => {
              this.$modal.notifySuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const noticeIds = row.noticeId || this.ids
      this.$modal.confirm('是否确认删除公告编号为"' + noticeIds + '"的数据项？').then(function() {
        return delNotice(noticeIds)
      }).then(() => {
        this.getList()
        this.$modal.notifySuccess('删除成功')
      }).catch(() => {
      })
    },
    /** 查询用户列表 */
    getUserList() {
      this.userList.length = 0
      userList(this.queryUserParams).then(response => {
        this.userList[0] = { userId: 0, nickName: '全部', userName: '全部' }
        this.userList = this.userList.concat(response.rows)
      })
    },
    /** 查看按钮操作 */
    handlePreview(row) {
      this.form = row
      this.centerDialogVisible = true
    },
    /** 接收通知人员反显 */
    getUserByIds(userIds) {
      let userNames = ''
      userIds.split(',').forEach(id => {
        this.userList.forEach(item => {
          if (item.userId === Number(id)) {
            userNames += userNames === '' ? item.nickName : ',' + item.nickName
          }
        })
      })
      return userNames
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
