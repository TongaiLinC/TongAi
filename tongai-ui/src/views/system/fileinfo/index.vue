<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="文件名称" prop="fileName">
        <el-input
            v-model="queryParams.fileName"
            placeholder="请输入文件名称"
            clearable
            @keyup.enter.native="handleQuery"
        />
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
            v-hasPermi="['takj:fileinfo:add']"
        >上传文件
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
            v-hasPermi="['takj:fileinfo:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="fileInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="编号" width="55" align="center" prop="fileId"/>
      <el-table-column label="唯一编码" align="center" prop="identifier"/>
      <el-table-column label="名称" align="center" prop="fileName">
        <template slot-scope="scope">
          <el-tooltip effect="dark" placement="top" content="点击复制完整文件地址">
            <el-button type="text" v-clipboard:copy="getFileALlUrl(scope.row.filePath)"
                       v-clipboard:success="clipboardSuccess"
            >{{ scope.row.fileName }}
            </el-button>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="类别" width="80" align="center" prop="fileType"/>
      <el-table-column label="大小" width="120" align="center" prop="fileSize">
        <template slot-scope="scope">
          {{ formatFileSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column label="上传时间" align="center" prop="createTime"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handlePreview(scope.row)"
          >预览
          </el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['takj:fileinfo:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"
    />

    <!-- 上传文件对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="450px" append-to-body>
      <file-upload-chunk v-if="open" v-model="filePath"/>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog v-if="previewVisible" :visible.sync="previewVisible" :width="previewType==='other'?'450px':'800px'" append-to-body>
      <img v-if="previewType === 'image'" :src="previewUrl" style="width: 100%;">
      <video v-else-if="previewType === 'video'" :src="previewUrl" controls style="width: 100%"></video>
      <iFrame v-else-if="previewType === 'pdf'" :src="previewUrl" style="height: 80vh"></iFrame>
      <div v-else style="text-align: center">
        <p>不支持预览此文件类型</p>
        <el-button type="primary" @click="downloadFile">下载文件</el-button>
      </div>
    </el-dialog>
  </div>

</template>

<script>
import { delFileinfo, listFileinfo } from '@/api/system/fileinfo'
import { formatFileSize } from '@/utils/tongai'
import iFrame from '@/components/iFrame/index.vue'

export default {
  name: 'Fileinfo',
  components: { iFrame },
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
      // 文件管理表格数据
      fileInfoList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      previewVisible: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fileName: null,
        fileType: null
      },
      // 表单参数
      form: {},
      filePath: '',
      previewType: '',
      previewUrl: '',
      baseUrl: process.env.VUE_APP_BASE_API,
    }
  },
  created() {
    this.getList()
  },
  methods: {
    formatFileSize,
    /** 查询文件管理列表 */
    getList() {
      this.loading = true
      listFileinfo(this.queryParams).then(response => {
        this.fileInfoList = response.rows
        this.total = response.total
        this.loading = false
      })
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
      this.ids = selection.map(item => item.fileId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.filePath = ''
      this.open = true
      this.title = '上传文件'
    },
    // 预览文件（调用子组件方法）
    handlePreviewFile(row) {
      const { filePath, fileName, fileSize } = row
      this.$refs.fileUploadChunk.handlePreview({ url: filePath, fileName: fileName, fileSize: fileSize })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const fileIds = row.fileId || this.ids
      this.$modal.confirm('是否确认删除文件管理编号为"' + fileIds + '"的数据项？').then(function() {
        return delFileinfo(fileIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    },
    // 获取文件地址
    getFileALlUrl(path) {
      return location.origin + this.baseUrl + path;
    },
    /** 复制成功 */
    clipboardSuccess() {
      this.$modal.msgSuccess('复制成功')
    },
    // 文件预览处理
    handlePreview(result) {
      if (!result?.filePath) return
      const url = result.filePath
      const ext = url.split('.').pop().toLowerCase()
      this.previewUrl = url
      if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) {
        this.previewType = 'image'
      } else if (['mp4', 'webm', 'ogg'].includes(ext)) {
        this.previewType = 'video'
      } else if (['pdf'].includes(ext)) {
        this.previewType = 'pdf'
      } else {
        this.previewType = 'other'
      }
      this.previewUrl = this.baseUrl + url
      this.previewVisible = true
    },
    // 文件下载处理
    downloadFile() {
      if (!this.uploadResult?.url) return
      const a = document.createElement('a')
      a.href = this.uploadResult.url
      a.download = this.uploadResult.filename
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
    },
  }
}
</script>
