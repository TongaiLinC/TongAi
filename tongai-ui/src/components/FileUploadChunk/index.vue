template
<template xmlns="http://www.w3.org/1999/html">
  <div class="file-upload">
    <!-- 文件上传组件 -->
    <el-upload
        :multiple="multiple"
        class="upload_file_Chunk"
        drag
        action="#"
        :file-list="fileList"
        :limit="limit"
        :show-file-list="false"
        :before-upload="beforeUpload"
        :on-exceed="handleExceed"
        :http-request="customUpload"
        :disabled="uploading"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">
        请上传
        <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>最大限制数量为 <b style="color: #f56c6c">{{ limit }}个</b></template>
        <br>
        <template v-if="fileType"> 格式为 <b style="color: #f56c6c">{{ fileType.join('/') }}</b></template>
        的文件
      </div>
      <!-- 上传进度展示 -->
      <el-progress v-if="uploading" :percentage="progressPercent" :status="uploadStatus"></el-progress>
    </el-upload>

    <!-- 文件信息展示 -->
    <div class="file-info" v-for="item in fileList">
      <div class="file-name">{{ item.fileInfo.name }}</div>
      <div class="file-size">{{ formatFileSize(item.fileInfo.size) }}</div>
      <!-- 上传结果展示 -->
      <div v-if="item.uploadResult" class="upload-result">
        <span v-if="item.uploadResult.success" class="success">
          <i class="el-icon-success"></i> 上传成功
        </span>
        <span v-else class="error">
          <i class="el-icon-error"></i> {{ item.uploadResult.message }}
        </span>
        <!-- 文件预览按钮 -->
        <el-button v-if="item.uploadResult.url" type="text" @click="handlePreview(item.uploadResult)">预览</el-button>
      </div>
    </div>

    <!-- 文件预览对话框 -->
    <el-dialog :visible.sync="previewVisible" width="600px" append-to-body>
      <img v-if="previewType === 'image'" :src="previewUrl" style="width: 100%">
      <video v-else-if="previewType === 'video'" :src="previewUrl" controls style="width: 100%"></video>
      <div v-else>
        <p>不支持预览此文件类型</p>
        <el-button type="primary" @click="downloadFile">下载文件</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import SparkMD5 from 'spark-md5'
import { checkFile, mergeChunks, upload, uploadChunk } from '@/api/system/fileinfo'
import { formatFileSize } from '@/utils/tongai'

export default {
  name: 'FileUploadChunk',
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    // 值
    value: [String, Object, Array],
    // 上传限制
    multiple: {
      type: Boolean,
      default: true
    },
    // 数量限制
    limit: {
      type: Number,
      default: 50
    },
    // 文件类型
    fileType: {
      type: Array,
      default: () => ['doc', 'xls', 'docx', 'xlsx', 'ppt', 'txt', 'pdf', 'jpg', 'jpeg', 'png','mp4']
    },
    // 文件大小
    fileSize: {
      type: Number,
      default: 50
    },
    // 分片大小 默认2M
    chunkSize: {
      type: Number,
      default: 2
    },
    // 文件分片大小 默认10M
    fileChunkSize: {
      type: Number,
      default: 10
    }
  },
  data() {
    return {
      file: null,
      number: 0,
      uploading: false,
      progressPercent: 0,
      uploadStatus: null,
      uploadResult: null,
      previewVisible: false,
      previewUrl: '',
      previewType: '',
      baseUrl: process.env.VUE_APP_BASE_API,
      fileList: []
    }
  },
  methods: {
    formatFileSize,
    // 上传前处理
    beforeUpload(file) {
      // 校检文件类型
      if (this.fileType) {
        const extList = file.name.split('.')
        const fileExt = extList[extList.length - 1]
        const isTypeOk = this.fileType.indexOf(fileExt) >= 0
        if (!isTypeOk) {
          this.$modal.msgError(`文件格式不正确, 请上传${this.fileType.join('/')}格式文件!`)
          return false
        }
      }

      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize
        if (!isLt) {
          this.$modal.msgError(`上传文件大小不能超过 ${this.fileSize} MB!`)
          return false
        }
      }

      this.file = file
      this.uploading = true
      this.progressPercent = 0
      this.uploadStatus = null
      this.uploadResult = null

      this.$modal.loading('正在上传文件，请稍候...')
      return true
    },
    // 文件个数超出
    handleExceed() {
      this.$modal.msgError(`上传文件数量不能超过 ${this.limit} 个!`)
    },
    // 自定义上传方法
    async customUpload(options) {
      const { file } = options
      const fileName = file.name
      const fileExt = file.name.split('.')[1]
      const CHUNK_SIZE_1 = this.chunkSize * 1024 * 1024
      try {
        // 计算文件MD5
        const fileMd5 = await this.calculateFileMd5(file)
        // 检查文件是否已存在
        const checkRes = await checkFile({
          identifier: fileMd5,
          fileName: file.name
        })

        if (checkRes.uploaded) {
          // 秒传
          this.progressPercent = 100
          this.uploadStatus = 'success'
          this.uploadResult = {
            success: true,
            url: checkRes.url,
            fileName: file.name
          }
          return
        }

        if (file.size <= this.fileChunkSize * 1024 * 1024) {
          // 小文件直接上传
          const formData = new FormData()
          formData.append('file', file)
          formData.append('identifier', fileMd5)
          formData.append('fileType', fileExt)
          formData.append('mimeType', file.type)
          formData.append('fileName', fileName)
          formData.append('fileSize', file.size)

          const uploadRes = await upload(formData, event => {
            this.progressPercent = Math.floor((event.loaded / event.total) * 100)
          })

          this.uploadStatus = 'success'
          this.uploadResult = {
            success: true,
            url: uploadRes.url,
            fileName: uploadRes.fileName
          }

        } else {
          // 大文件分片上传
          const chunkCount = Math.ceil(file.size / CHUNK_SIZE_1)
          let uploadedChunks = checkRes.uploadedChunks || []

          for (let i = 0; i < chunkCount; i++) {
            if (uploadedChunks.includes(i + 1)) {
              this.progressPercent = Math.floor(((i + 1) / chunkCount) * 100)
              continue
            }

            const start = i * CHUNK_SIZE_1
            const end = Math.min(file.size, start + CHUNK_SIZE_1)
            const chunk = file.slice(start, end)

            const chunkFormData = new FormData()
            chunkFormData.append('file', chunk)
            chunkFormData.append('chunkNumber', i + 1)
            chunkFormData.append('chunkSize', CHUNK_SIZE_1)
            chunkFormData.append('identifier', fileMd5)
            chunkFormData.append('totalChunks', chunkCount)
            chunkFormData.append('fileName', fileName)
            chunkFormData.append('fileSize', file.size)
            await uploadChunk(chunkFormData)
            this.progressPercent = Math.floor(((i + 1) / chunkCount) * 100)
          }

          // 合并分片
          const mergeRes = await mergeChunks({
            identifier: fileMd5,
            fileName: file.name,
            fileSize: file.size,
            totalChunks: chunkCount
          })

          this.uploadStatus = 'success'
          this.uploadResult = {
            success: true,
            url: mergeRes.url,
            fileName: mergeRes.fileName
          }
        }
      } catch (error) {
        this.uploadStatus = 'exception'
        this.uploadResult = {
          success: false,
          message: error.msg || '上传失败'
        }
      } finally {
        // 上传成功后
        if (this.uploadResult.success) {
          this.fileList.push({ fileInfo: options.file, uploadResult: this.uploadResult })
          console.log('fileList', this.fileList)
          this.emitChangeEvent(this.uploadResult)
        }
        this.$emit('getPath')
        this.$modal.closeLoading()
        this.uploading = false
      }
    },

    // 计算文件MD5
    calculateFileMd5(file) {
      return new Promise((resolve, reject) => {
        const spark = new SparkMD5.ArrayBuffer()
        const fileReader = new FileReader()
        const chunkSize = 2 * 1024 * 1024 // 2MB
        const chunks = Math.ceil(file.size / chunkSize)
        let currentChunk = 0

        fileReader.onload = e => {
          spark.append(e.target.result)
          currentChunk++

          if (currentChunk < chunks) {
            loadNext()
          } else {
            resolve(spark.end())
          }
        }

        fileReader.onerror = e => {
          reject(new Error('文件读取失败'))
        }

        function loadNext() {
          const start = currentChunk * chunkSize
          const end = Math.min(file.size, start + chunkSize)
          fileReader.readAsArrayBuffer(file.slice(start, end))
        }

        loadNext()
      })
    },

    // 文件预览处理
    handlePreview(result) {
      if (!result?.url) return
      const url = result.url
      const ext = url.split('.').pop().toLowerCase()
      this.previewUrl = url
      if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) {
        this.previewType = 'image'
      } else if (['mp4', 'webm', 'ogg'].includes(ext)) {
        this.previewType = 'video'
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
    // 添加emitChangeEvent方法
    emitChangeEvent(res) {
      let uploadList = []
      // 如果当前value是字符串，转换为数组
      if (typeof this.value === 'string') {
        if (this.value) {
          uploadList = this.value.split(',').map(url => ({ url }))
        }
      } else if (Array.isArray(this.value)) {
        uploadList = [...this.value]
      } else if (this.value) {
        uploadList = [this.value]
      }
      // 添加新文件
      uploadList.push({ name: res.fileName, url: res.url })
      // 触发v-model更新
      this.$emit('change', this.listToString(uploadList))
    },
    // 对象转成指定字符串分隔
    listToString(list, separator) {
      let strs = ''
      separator = separator || ','
      for (let i in list) {
        strs += list[i].url + separator
      }
      return strs != '' ? strs.substr(0, strs.length - 1) : ''
    }
  }
}
</script>

<style scoped>
.file-upload {
  padding: 20px;
}

.file-info {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.file-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.file-size {
  color: #999;
  font-size: 12px;
  margin-bottom: 10px;
}

.upload-result {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #eee;
}

.upload-result .success {
  color: #67c23a;
}

.upload-result .error {
  color: #f56c6c;
}
</style>
