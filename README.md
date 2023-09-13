<p align="center">
	<img alt="logo" style="width: 64px" src="https://www.cztaj.cn/logo.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">同乂管理后台 v1.0.0</h1>
<h4 align="center">基于Ruoyi-vue前后端分离版的Java快速开发进行二次的开发和完善</h4>
<p align="center">
	<a href="https://gitee.com/y_project/RuoYi-Vue/stargazers"><img src="https://gitee.com/y_project/RuoYi-Vue/badge/star.svg?theme=dark"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue"><img src="https://img.shields.io/badge/RuoYi-v3.8.6-brightgreen.svg"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>



## 平台简介

同乂管理后台是一套全部开源的快速开发脚手架，基于RuoYi-Vue进行二次开发，在此基础上进行优化和升级,集成更多的开发插件，帮助开发着更快的上手和开发使用。

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10.  登录日志：系统登录日志记录查询包含登录异常。
11.  在线用户：当前系统中活跃用户状态监控。
12.  定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13.  代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14.  系统接口：根据业务代码自动生成相关的api接口文档。
15.  服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16.  缓存监控：对系统的缓存信息查询，命令统计等。
17.  在线构建器：拖动表单元素生成相应的HTML代码。
18.  连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 优化功能

1. 代码生成：自定义路径默认`/`生成到项目根目录下对应的文件中，
2. 代码生成：支持一键执行菜单生成，
3. 代码生成自定选项：
    1. 支持选择是否生成前端相关代码
    2. 支持选择是否添加数据权限设置
    3. 支持选择是否为逻辑删除

4. 添加`lombok`依赖支持，
5. 升级`mybatis`插件为`mybatis-plus`插件(生成代码时原代码基础上添加支持)
6. 通知公告功能完善：
    1. 登陆后提示未读消息
    2. 添加通知重要等级
    3. 发布功能，未发布前可编辑，发布后不可编辑
    4. 指定用户通知功能(可通知人员为数据权限范围内的人员)
    5. 用户消息列表，未读消息列表
    6. 通知公告详情查看功能

7. 部分弹窗改为抽屉样式(日志详情页等)
8. 日志详情中`请求参数`和`请求结果`格式化展示

## 完善中。。。
- [ ] 部门级通知(部门中成员可看)
- [ ] 实时消息接收提示功能（websocket/rabbitmq）
- [ ] 指定人员或部门级通知可查看未读人数
- [ ] 文件管理系统，接入阿里云、七牛云、腾讯云等
- [ ] 关键词过滤功能
- [ ] 前端敏感信息处理功能
- [ ] 接入微信相关组件等

## 修改/新增功能的演示图

### 1、操作日志详情展示
<table>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/操作日志详情.png"/>		
    </td>
  </tr>
</table>

### 2、通知公告功能展示
<table>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/首页通知.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/未读通知列表.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/通知列表.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/消息中心.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/通知类别.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/通知等级.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/通知人员.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/通知详情.png"/>
    </td>
  </tr>
</table>

### 3、定时任务展示
<table>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/定时任务列表.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/定时任务编辑.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/定时任务详情.png"/>
    </td>
  </tr>
</table>

### 4、代码生相关功能展示
<table>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/代码生成列表.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/代码生成编辑.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/代码生成详细.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/数据权限开.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/数据权限开1.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/数据权限关.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/数据权限关1.png"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/逻辑删除开.png"/>
    </td>
    <td>
      <img src="https://www.cztaj.cn/oneblog/markdown/逻辑删除关.png"/>
    </td>
  </tr>
</table>

