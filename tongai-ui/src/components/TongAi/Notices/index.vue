<template>
  <div>
    <svg-icon icon-class="notices" @click="handleVisibleDrawer"/>
    <el-drawer
        title="未读消息"
        :visible.sync="visibleDrawer"
        direction="rtl"
        size="450px"
        append-to-body
        custom-class="notice_drawer"
    >
      <template slot="title">
        <span style="font-size: 18px;color: #000000">未读消息</span>
      </template>
      <el-divider class="divider_custom"></el-divider>
      <el-row :gutter="10">
        <el-col :span="23">
          <el-button
              style="float: right"
              type="primary"
              plain size="mini"
              icon="el-icon-delete"
              @click="handleUpdateAllRead"
              :disabled="noticeList.length === 0"
          >
            全部已读
          </el-button>
        </el-col>
      </el-row>
      <div class="notices" v-loading="loading">
        <template v-for="(item, index) in noticeList">
          <div class="notice_item">
            <el-link :type="getWeight(item.weight).type" class="notices_item__title" @click="goto(item.noticeId)">
              {{ item.noticeTitle }}
            </el-link>
            <el-badge class="notices_item__badge" :type="getWeight(item.weight).type"
                      :value="getWeight(item.weight).weight"
            >
            </el-badge>
            <div class="notices_item__content">
              <span class="notices_item__type">通知类别：
                <dict-tag :options="dict.type.sys_notice_type" :value="item.noticeType"/>
              </span>
              <span class="notices_item__time">{{ formatDateTime(item.publishTime) }}</span>
            </div>
            <el-divider class="notices_item__divider"></el-divider>
          </div>
        </template>

      </div>
    </el-drawer>
  </div>
</template>

<script>
import {getUnReadCount, listUnReadNotice, setAllNoticeRead} from '@/api/system/notice'
import {formatTime} from '@/utils'

export default {
  name: 'Notices',
  dicts: ['sys_notice_weight', 'sys_notice_type'],
  data() {
    return {
      name: 'Notice/read',
      loading: false,
      visibleDrawer: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 30
      },
      noticeList: []
    }
  },
  created() {
  },
  methods: {
    // 时间格式化，几天前，几小时前
    formatDateTime(time) {
      if (time) {
        return formatTime(Date.parse(new Date(time).toString()), '')
      }
      return ''
    },
    // 跳转页面
    goto(params) {
      if (params) {
        this.visibleDrawer = false
        this.$router.push({ name: this.name, params: { id: params } })
      }
    },
    handleUpdateAllRead() {
      let ids = this.noticeList.map(item => item.noticeId)
      if (ids.length === 0) {
        return
      }
      setAllNoticeRead(ids).then(res => {
        this.$notify.success('设置成功')
        this.getUnReadList()
        this.getUnReadCount()
      })
    },
    /** 获取未读通知个数阅读状态，通过store进行状态管理 */
    getUnReadCount() {
      getUnReadCount().then((res) => {
        this.$store.commit('SET_UNREAD_COUNT', res)
      })
    },
    handleVisibleDrawer() {
      this.visibleDrawer = true
      this.getUnReadList()
    },
    /** 获取重要等级 */
    getWeight(weight) {
      let types = ['info', 'primary', 'warning', 'danger']
      let type = types[Number(weight) - 1]
      weight = this.selectDictLabel(this.dict.type.sys_notice_weight, weight)
      return {
        type: type,
        weight: weight
      }
    },
    /** 查询未读公告列表 */
    getUnReadList() {
      this.loading = true
      listUnReadNotice(this.queryParams).then(response => {
        this.noticeList = response.rows
        this.loading = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.divider_custom {
  margin-top: 0px;
  margin-bottom: 10px;
}

.notices {
  padding: 25px;
  font-size: 14px;
  color: #909399;

  .notices_item__title {
    font-size: 16px;
    font-weight: bold;
  }

  .notices_item__content {
    width: 100%;
    margin-top: 20px;
    display: inline-flex;
    align-items: center;
    justify-content: space-between;

    .notices_item__type {
      display: inline-flex;
      align-items: center;
    }

    .notices_item__time {
    }
  }

  .notices_item__divider {
    margin-top: 5px;
  }

  .notices_item__badge {
    .el-badge__content.el-badge__content--info.is-fixed {
      right: 0px;
    }
  }
}
</style>
