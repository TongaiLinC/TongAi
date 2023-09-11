import {getInfo, login, logout} from '@/api/login'
import {getToken, removeToken, setToken} from '@/utils/auth'
import {getUnReadCount} from '@/api/system/notice'
import {Notification} from 'element-ui'

const user = {
  state: {
    token: getToken(),
    name: '',
    nickName: '',
    avatar: '',
    roles: [],
    permissions: [],
    unReadCount: 0
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_NICK_NAME: (state, nickName) => {
      state.nickName = nickName
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
    },
    SET_UNREAD_COUNT: (state, unReadCount) => {
      state.unReadCount = unReadCount
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid).then(res => {
          setToken(res.token)
          // 获取未读通知数量
          setTimeout(() => {
            getUnReadCount().then((res) => {
              if (res > 0) {
                Notification.info({
                  title: '消息中心',
                  dangerouslyUseHTMLString: true,
                  message: '您有<span style="font-size: 26px;color: #ff0000">' + res + '</span>条未读消息，请前往消息中心查看！',
                  offset: 10,
                  duration: 2500
                })
              }
              commit('SET_UNREAD_COUNT', res)
            })
          },1200)
          commit('SET_TOKEN', res.token)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo().then(res => {
          const user = res.user
          const avatar = (user.avatar == '' || user.avatar == null) ? require('@/assets/images/profile.jpg') : process.env.VUE_APP_BASE_API + user.avatar
          if (res.roles && res.roles.length > 0) { // 验证返回的roles是否是一个非空数组
            commit('SET_ROLES', res.roles)
            commit('SET_PERMISSIONS', res.permissions)
          } else {
            commit('SET_ROLES', ['ROLE_DEFAULT'])
          }
          commit('SET_NAME', user.userName)
          commit('SET_NICK_NAME', user.nickName)
          commit('SET_AVATAR', avatar)
          getUnReadCount().then((res) => {
            commit('SET_UNREAD_COUNT', res)
          })
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          commit('SET_NICK_NAME', '')
          commit('SET_UNREAD_COUNT', 0)
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    }
  }
}

export default user
