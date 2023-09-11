## 开发

```bash
# 克隆项目
git clone https://gitee.com/y_project/RuoYi-Vue

# 进入项目目录
cd tongai-ui

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
# 推荐使用 nrm 这个npm源管理插件方便测试不同源的速度并切换npm各种源，详情可参考：

npm install --registry=https://registry.npmmirror.com

# 启动服务
npm run dev
```

浏览器访问：
> `windows`：http://localhost:80
> 
>`Mac（由于权限原因无法直接访问80端口）`：http://localhost:1024
> 
> MAC若需要访问80端口需要使用`sudo`命令启动

## 发布

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```
