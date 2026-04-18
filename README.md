# 高校社团管理系统

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen)
![Vue](https://img.shields.io/badge/Vue-3.5-green)

## 项目简介

高校社团管理系统是一个基于 Spring Boot + Vue 3 的全栈 Web 应用，旨在帮助高校实现社团信息化管理。系统支持管理员、社团负责人、学生等多种角色，提供社团管理、活动管理、成员管理、审批流程等核心功能。

## 技术栈

### 后端
- **框架**: Spring Boot 3.4.3
- **数据库**: MySQL 8.0
- **ORM**: MyBatis 3.0.4
- **认证**: JWT (jjwt 0.12.6)
- **安全**: Spring Security Crypto
- **Java**: 17
- **构建工具**: Maven 3.6+

### 前端
- **框架**: Vue 3.5
- **构建工具**: Vite 8
- **UI 组件库**: Element Plus 2.13
- **路由**: Vue Router 5
- **HTTP 客户端**: Axios 1.13

## 项目结构

```
club_final/
├── club-backend/              # 后端项目
│   ├── src/main/java/com/club/
│   │   ├── common/           # 公共类
│   │   │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   ├── PageResult.java              # 分页结果
│   │   │   └── Result.java                  # 统一响应格式
│   │   ├── config/           # 配置类
│   │   │   ├── CorsConfig.java              # 跨域配置
│   │   │   ├── JwtConfig.java               # JWT 配置
│   │   │   ├── JwtInterceptor.java          # JWT 拦截器
│   │   │   ── WebMvcConfig.java            # MVC 配置
│   │   ├── controller/       # 控制器层
│   │   │   ├── AuthController.java          # 用户认证
│   │   │   ├── ClubController.java          # 社团管理
│   │   │   ├── ActivityController.java      # 活动管理
│   │   │   ├── AnnouncementController.java  # 公告管理
│   │   │   ├── ApprovalController.java      # 审批管理
│   │   │   ├── UserController.java          # 用户管理
│   │   │   ├── StatisticsController.java    # 数据统计
│   │   │   └── OperationLogController.java  # 操作日志
│   │   ├── entity/           # 实体类
│   │   ├── mapper/           # MyBatis Mapper
│   │   └── service/          # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yml   # 配置文件
│   └── pom.xml
├── club-management/           # 前端项目
│   ├── src/
│   │   ├── api/              # API 接口封装
│   │   ├── components/       # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── utils/            # 工具函数
│   │   ── views/            # 页面组件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── test_data.sql             # 测试数据
├── full_test_data.sql        # 完整测试数据
├── 类图.puml                 # 系统类图
├── start.sh                  # 一键启动脚本
└── README.md                 # 项目文档
```

## 核心功能

### 管理员功能
| 模块 | 功能 |
|------|------|
| 社团管理 | 社团审核、社团信息维护、社团排名 |
| 活动管理 | 活动审核、活动统计、活动数据导出 |
| 用户管理 | 用户角色分配、权限管理、用户信息管理 |
| 公告管理 | 发布、编辑、删除公告 |
| 审批管理 | 处理各类审批申请（社团创建、活动发布等） |
| 数据统计 | 社团活跃度、活动参与率、用户增长趋势 |
| 操作日志 | 系统操作记录查询与导出 |
| 角色管理 | 角色权限分配与管理 |

### 社团负责人功能
| 模块 | 功能 |
|------|------|
| 社团管理 | 编辑社团资料、社团信息维护 |
| 活动管理 | 创建、编辑、管理社团活动 |
| 成员管理 | 审批加入申请、管理成员角色 |
| 公告管理 | 发布社团内部公告 |
| 招新管理 | 管理社团招新活动 |

### 学生功能
| 模块 | 功能 |
|------|------|
| 社团广场 | 浏览社团、搜索社团、查看社团详情 |
| 我的社团 | 查看已加入的社团、退出社团 |
| 活动中心 | 浏览活动、报名参与活动、查看报名状态 |
| 公告浏览 | 查看社团公告 |
| 个人中心 | 个人信息管理 |

## 环境要求

| 软件 | 版本要求 |
|------|----------|
| Java | 17+ |
| Node.js | 16+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |

## 快速开始

### 方式一：一键启动（推荐）

在项目根目录执行：

```bash
./start.sh
```

脚本会自动依次启动后端和前端，访问 `http://localhost:3000` 即可。

按 `Ctrl+C` 可同时停止两个服务。

### 方式二：分别启动

#### 1. 后端启动

```bash
cd club-backend

# 启动项目
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

#### 2. 前端启动

```bash
cd club-management

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

### 3. 访问系统

打开浏览器访问 `http://localhost:3000`，使用以下默认账号登录：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 社团负责人 | president | president123 |
| 学生 | student | student123 |

## 配置说明

### 后端配置

配置文件位于 `club-backend/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/School-Club-Management-System
    username: root
    password: your_password

jwt:
  secret: your-secret-key
  expiration: 86400000  # token 过期时间（毫秒）
```

可通过环境变量覆盖配置：

```bash
export DB_URL=jdbc:mysql://localhost:3306/your_database
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key
```

### 前端配置

前端已配置 Vite 代理，`/api` 请求会自动转发到后端：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## API 文档

主要 API 接口：

### 认证模块
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |

### 社团模块
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/clubs` | GET | 获取社团列表 |
| `/api/clubs/{id}` | GET | 获取社团详情 |
| `/api/clubs` | POST | 创建社团 |
| `/api/clubs/{id}` | PUT | 更新社团信息 |

### 活动模块
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/activities` | GET | 获取活动列表 |
| `/api/activities/{id}` | GET | 获取活动详情 |
| `/api/activities` | POST | 创建活动 |
| `/api/activities/{id}` | PUT | 更新活动信息 |

### 审批模块
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/approvals` | GET | 获取审批列表 |
| `/api/approvals/{id}` | GET | 获取审批详情 |
| `/api/approvals/{id}/approve` | PUT | 审批通过 |
| `/api/approvals/{id}/reject` | PUT | 审批驳回 |

### 公告模块
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/announcements` | GET | 获取公告列表 |
| `/api/announcements/{id}` | GET | 获取公告详情 |
| `/api/announcements` | POST | 创建公告 |

### 统计模块
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/statistics/clubs` | GET | 获取社团统计 |
| `/api/statistics/activities` | GET | 获取活动统计 |

## 数据库设计

主要数据表：

| 表名 | 说明 |
|------|------|
| `users` | 用户表 |
| `clubs` | 社团表 |
| `activities` | 活动表 |
| `club_members` | 社团成员表 |
| `activity_signups` | 活动报名表 |
| `approvals` | 审批表 |
| `announcements` | 公告表 |
| `operation_logs` | 操作日志表 |

## 常见问题

### 1. 后端启动失败
- 检查 MySQL 是否正常运行
- 检查 `application.yml` 中数据库连接配置
- 检查端口 8080 是否被占用

### 2. 前端无法连接后端
- 确认后端服务已启动
- 检查 `vite.config.js` 中代理配置
- 确认前端请求地址正确

### 3. 登录失败
- 检查数据库测试数据是否导入
- 确认用户名密码正确

## 开发团队

| 成员 | 学号 | 班级 |
|------|------|------|
| 奚梓恒 | 2412001158 | 软件工程5班 |
| 马虹华 | - | 软件工程5班 |
| 王宇航 | - | 软件工程5班 |
| 马子健 | - | 软件工程5班 |

## 许可证

本项目仅供学习和研究使用。
