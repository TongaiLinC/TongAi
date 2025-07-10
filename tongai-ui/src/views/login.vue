<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">{{ title }}</h3>
      <el-tabs v-model="activeName" type="border-card" style="border-radius: 10pxmargin-bottom: 15px">
        <el-tab-pane label="账号登录" name="userAccount">
          <el-form-item prop="username">
            <el-input
                v-model="loginForm.username"
                type="text"
                auto-complete="off"
                placeholder="账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon"/>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                v-model="loginForm.password"
                type="password"
                auto-complete="off"
                placeholder="密码"
                @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon"/>
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                style="width: 63%"
                @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon"/>
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
        </el-tab-pane>
        <el-tab-pane label="手机登录" name="userPhone" v-if="smsCodeEnabled">
          <el-form-item prop="phone">
            <el-input
                v-model="loginForm.phone"
                type="text"
                auto-complete="off"
                placeholder="手机号"
            >
              <template slot="prepend">+86</template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-input
                v-model="loginForm.code"
                type="text"
                auto-complete="off"
                placeholder="验证码"
                @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon"/>
              <template slot="append">
                <el-button type="text" :disabled="disable" style="width: 70px;margin-left: 5px;margin-right: 5px" @click="getSmsCode">{{ codeText }}</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
      <el-form-item style="width:100%;">
        <el-row :gutter="5">
          <el-col :span="register?12:24">
            <el-button
                round
                :loading="loading"
                size="medium"
                type="primary"
                style="width:100%;margin-top: 15px"
                @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-col>
          <el-col :span="12" v-if="register">
            <el-button
                round
                size="medium"
                style="width:100%;"
                @click.native.prevent="handleLogin"
            >
              <router-link :to="'/register'">注册</router-link>
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2018-2025 tongai.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg, getSmsCode } from '@/api/login'
import Cookies from 'js-cookie'
import {decrypt, encrypt} from '@/utils/jsencrypt'
import { validPhone } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    var validatePhone = (rule, value, callback) => {
      if (this.activeName === 'userPhone' && value === '') {
        callback(new Error('请输入手机号'))
      } else {
        if (this.activeName === 'userPhone' && this.loginForm.phone !== '') {
          if (!validPhone(this.loginForm.phone)) {
            callback(new Error('请输入正确的手机号'))
          }
        }
        callback()
      }
    }
    var validateUserName = (rule, value, callback) => {
      if (this.activeName === 'userAccount' &&value === '') {
        callback(new Error('请输入用户名'))
      }
      callback()
    }
    var validatePassword = (rule, value, callback) => {
      if (this.activeName === 'userAccount' &&value === '') {
        callback(new Error('请输入密码'))
      }
      callback()
    }
    var validateCode = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入验证码'))
      } else {
        callback()
      }
    }
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: '',
      codeText: '获取验证码',
      disable: false,
      activeName: 'userAccount',
      loginForm: {
        username: 'admin',
        password: 'admin123',
        phone: '',
        rememberMe: false,
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [
          { required: true, trigger: 'blur', validator: validateUserName}
        ],
        password: [
          { required: true, trigger: 'blur', validator: validatePassword }
        ],
        phone: [
            { required: true, trigger: 'blur', validator: validatePhone }
        ],
        code: [
            { required: true, trigger: 'change',validator: validateCode }
        ]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      smsCodeEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        this.smsCodeEnabled = res.smsCodeEnabled === undefined ? true : res.smsCodeEnabled
        if (this.captchaEnabled) {
          this.codeUrl = 'data:image/gif;base64,' + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getSmsCode() {
      if (!validPhone(this.loginForm.phone)) {
        this.$message.error('请输入正确的手机号')
        return
      }
      this.disable = true
      let times = 60
      getSmsCode(this.loginForm.phone).then(res => {
        this.$message.success(res.msg)
        let time = setInterval(() => {
          if (times === 0){
            clearInterval(time)
            this.disable = false
            this.codeText = '获取验证码'
            return
          }
          times = times - 1
          this.codeText = times + "秒后重试"
        },1000)
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set('username', this.loginForm.username, { expires: 30 })
            Cookies.set('password', encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove('username')
            Cookies.remove('password')
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch('Login', this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || '/' }).catch(() => {
            })
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    },
    handleClick(tab, event) {

    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 10px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  z-index: 1;

  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 38px;
}
</style>
