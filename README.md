# jssh-agent

一个基于 Java 开发的 SSH 连接与服务器管理工具：通过浏览器即可连接远程主机，提供终端交互、系统监控、进程/文件/网络/服务管理能力。

## 徽章

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3-brightgreen.svg)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.14.5-409eff.svg)](https://element-plus.org/)
[![Apache MINA SSHD](https://img.shields.io/badge/Apache%20MINA%20SSHD-2.19.0-orange.svg)](https://mina.apache.org/sshd-project/)

## 展示图

<!-- 展示图：请在下方添加项目界面截图或 GIF -->

## 功能列表

| 功能模块 | 说明 |
| --- | --- |
| 终端交互 | 基于 xterm + WebSocket 的实时终端，支持持久化 Shell 会话 |
| 系统监控 | 内存占用（实时折线图）、CPU 使用率、网络流量 |
| 进程管理 | 进程列表（按 CPU / 内存降序）、进程详情查看 |
| 文件管理 | 远程目录懒加载浏览（`/proc`、`/etc` 等任意路径） |
| 网络管理 | 网卡信息：IPv4/IPv6、MAC、MTU、RX/TX 收发统计 |
| 服务管理 | systemctl 服务单元列表（加载/激活/子状态/描述） |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 18+（前端）
- 一台可 SSH 访问的远程 Linux 主机

### 启动后端

```bash
# 在项目根目录
mvn spring-boot:run
```

后端默认运行于 `http://localhost:8080`。

### 启动前端

```bash
cd jssh-front-end
npm install
npm run dev
```

前端默认运行于 `http://localhost:5173`，打开浏览器访问即可。

### 建立连接

1. 打开前端页面，在连接表单中填写远程主机的**主机地址、用户名、密码或私钥路径**；
2. 点击提交（后端 `POST /base/connect` 建立并保持 SSH 会话）；
3. 连接成功后即可使用侧边栏的终端、监控、进程、文件、网络、服务管理等功能。

## 安装与使用

### 系统依赖

后端 SSH 能力由 [Apache MINA SSHD](https://mina.apache.org/sshd-project/) 提供，无需外部 SSH 客户端。前端使用 Vue 3 + Element Plus + xterm。

### 后端配置

在 `src/main/resources/application-local.yml` 中可配置默认连接信息：

```yaml
ssh:
  connect:
    user: "your-user"
    host: "your-host"
    password: ""            # 密码认证（可选）
    privateKeyPath: ""      # 私钥路径（与密码二选一）
```

### 接口说明

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/base/connect` | 建立 SSH 连接（`ConnectParam`） |
| GET | `/memory` | 内存信息 |
| GET | `/process` | 进程列表 |
| GET | `/file?path=/` | 目录懒加载 |
| GET | `/network` | 网卡信息 |
| GET | `/services` | 服务单元列表 |

### 使用示例

```bash
# 建立连接
curl -X POST http://localhost:8080/base/connect \
  -H "Content-Type: application/json" \
  -d '{"host":"192.168.1.100","user":"root","privateKeyPath":"/home/you/.ssh/id_rsa"}'

# 查询内存
curl http://localhost:8080/memory

# 浏览根目录
curl "http://localhost:8080/file?path=/"
```

## 贡献指南

欢迎提交 Issue 与 Pull Request。参与前请先阅读 [CONTRIBUTING.md](./CONTRIBUTING.md)（如有）。基本流程：

1. Fork 本仓库；
2. 创建特性分支 `git checkout -b feature/xxx`；
3. 提交改动 `git commit -m 'feat: xxx'`；
4. 推送到远端 `git push origin feature/xxx`；
5. 发起 Pull Request。

## 许可证

本项目基于 [MIT License](./LICENSE) 开源。
