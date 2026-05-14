# 高校社团管理系统

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen)
![Vue](https://img.shields.io/badge/Vue-3.5-green)
![Java](https://img.shields.io/badge/Java-17-orange)

一个面向高校场景的社团管理平台，覆盖管理员、指导老师、社团负责人、学生四类角色，支持社团管理、活动审批、招新报名、成员维护、公告发布、统计分析和操作审计。

## 项目亮点

- 基于 `Spring Security + JWT` 的统一认证鉴权链路，支持方法级权限控制。
- 引入 `tokenVersion` 失效机制，支持登出后旧 token 失效。
- 审批流支持步骤推进、审批历史留痕、归档和角色边界控制。
- 活动更新采用“变更申请”模型，避免直接覆盖已通过版本。
- 后端引入 DTO/VO 分层、统一异常处理和 Bean Validation 校验。
- 数据库补充外键、CHECK 约束和状态约束。
- 已接入基础 CI，自动执行后端测试与前端构建。

## 技术栈

### 后端

- Spring Boot 3.4.3
- Spring Security
- MyBatis 3.0.4
- MySQL 8.0
- JWT (`jjwt 0.12.6`)
- Java 17
- Maven

### 前端

- Vue 3.5
- Vite 8
- Vue Router 5
- Axios 1.13
- Element Plus 2.13

## 角色与功能

### 管理员

- 用户管理、角色分配、账号状态维护
- 社团审批、活动审批、公告管理
- 审批中心、审批归档、审批历史查看
- 全局统计、操作日志查看

### 指导老师

- 查看并处理自己指导范围内的审批
- 查看受权限约束的统计数据
- 参与多步审批的前置步骤

### 社团负责人

- 社团资料维护
- 活动创建、活动变更申请、报名管理、签到管理
- 招新计划管理
- 成员管理、入社申请处理
- 公告发布与维护

### 学生

- 浏览社团、活动、公告
- 提交入社申请
- 活动报名、取消报名、查看报名状态
- 查看个人资料与我的社团

## 项目结构

```text
club_final/
├── club-backend/                                   # Spring Boot 后端
│   ├── src/main/java/com/club/
│   │   ├── common/                                # 通用响应、异常、上下文
│   │   ├── config/                                # Security、JWT、CORS 等配置
│   │   ├── controller/                            # 接口层
│   │   ├── dto/                                   # 请求 DTO
│   │   ├── entity/                                # 实体对象
│   │   ├── mapper/                                # MyBatis Mapper
│   │   ├── service/                               # 业务逻辑
│   │   └── vo/                                    # 返回 VO
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/
│   │       └── schema.sql                         # 全量建表脚本
│   └── pom.xml
├── club-management/                               # Vue 3 前端
│   ├── src/api/
│   ├── src/components/
│   ├── src/router/
│   ├── src/utils/
│   ├── src/views/
│   ├── package.json
│   └── vite.config.js
├── .github/workflows/ci.yml                       # CI 工作流
├── start.sh                                       # 一键启动脚本
├── 类图.puml
└── README.md
```

## 环境要求

| 软件 | 版本要求 |
|------|----------|
| Java | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |

## 快速开始

### 1. 准备数据库

数据库初始化脚本：

- `/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/club-backend/src/main/resources/db/schema.sql`

项目统一使用数据库：`School-Club-Management-System`。

例如：

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/School-Club-Management-System?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false'
export DB_USERNAME='root'
export DB_PASSWORD='你的数据库密码'
export JWT_SECRET='至少32位的安全密钥'
```

### 2. 启动后端

```bash
cd /Users/yzh/Desktop/Documents/软件工程\ jit/社团管理系统/club_final/club-backend
JWT_SECRET='至少32位的安全密钥' mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`

### 3. 启动前端

```bash
cd /Users/yzh/Desktop/Documents/软件工程\ jit/社团管理系统/club_final/club-management
npm install
npm run dev
```

前端默认地址：`http://localhost:3000`

### 4. 一键启动

也可以在项目根目录执行：

```bash
./start.sh
```

## 默认数据与账号说明

仓库默认**不建议内置固定演示账号**。当前版本通过以下配置控制是否初始化默认账号：

```yaml
app:
  bootstrap:
    default-users:
      enabled: ${INIT_DEFAULT_USERS:false}
      password: ${INIT_DEFAULT_USER_PASSWORD:}
```

如果你确实需要本地演示账号，可以自行设置：

```bash
export INIT_DEFAULT_USERS=true
export INIT_DEFAULT_USER_PASSWORD='Demo123456'
```

不建议在真实部署环境开启这组配置。

## 核心安全与业务设计

### 权限与认证

- 使用 `SecurityFilterChain` 统一拦截 API 请求。
- 使用 `@EnableMethodSecurity` + `@PreAuthorize` 做方法级权限控制。
- JWT 中写入 `tokenVersion`，登出或敏感变更后可失效旧 token。
- 前端 token 已从 `localStorage` 调整为 `sessionStorage`。

### 审批流

- 支持社团成立审批、活动申请审批。
- 支持 `currentStep / totalSteps` 多步推进。
- 指导老师只能处理前置步骤，管理员不能跳过未完成前置步骤。
- 审批历史写入 `approval_histories`，支持前端查看历史轨迹。
- 审批记录采用归档思路，而不是直接物理删除。

### 活动与报名

- 活动修改采用 `activity_change_requests` 变更单模型。
- 已通过活动不会被直接覆盖，审批通过后才应用变更。
- 活动报名支持 `pending / approved / rejected / cancelled / attended` 状态流转。
- 报名管理支持审批与签到。

### 数据一致性

- 关键表补充了外键、CHECK 约束、状态枚举约束。
- 后端增加 DTO 校验与统一异常处理，减少非法数据入库。
- 用户对外返回统一通过 `UserVO`，避免敏感字段泄漏。

## 配置说明

后端配置文件位置：

[application.yml](/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/club-backend/src/main/resources/application.yml)

关键环境变量如下：

| 变量名 | 说明 |
|--------|------|
| `DB_URL` | 数据库连接串 |
| `DB_USERNAME` | 数据库用户名 |
| `DB_PASSWORD` | 数据库密码 |
| `JWT_SECRET` | JWT 密钥，不能为空，建议 32 位以上 |
| `MYBATIS_LOG_LEVEL` | MyBatis 日志级别 |
| `CORS_ALLOWED_ORIGINS` | 允许的前端来源，多个逗号分隔 |
| `CORS_ALLOW_CREDENTIALS` | 是否允许携带凭证 |
| `INIT_DEFAULT_USERS` | 是否初始化演示用户 |
| `INIT_DEFAULT_USER_PASSWORD` | 演示用户初始密码 |

## 常用接口

### 认证

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/login` | POST | 登录 |
| `/api/user/info` | GET | 获取当前用户信息 |
| `/api/logout` | POST | 登出并失效当前用户旧 token |

### 社团

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/clubs` | GET | 社团分页列表 |
| `/api/clubs/{id}` | GET | 社团详情 |
| `/api/clubs` | POST | 创建社团 |
| `/api/clubs/{id}` | PUT | 更新社团 |
| `/api/clubs/{id}/members` | GET | 成员列表 |

### 活动

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/activities` | GET | 活动分页列表 |
| `/api/activities` | POST | 创建活动 |
| `/api/activities/{id}` | PUT | 更新活动或提交变更申请 |
| `/api/activities/{id}/signup` | POST | 活动报名 |
| `/api/activities/{id}/signups` | GET | 报名列表 |
| `/api/activities/{id}/signups/{signupId}/status` | PUT | 审核报名 |
| `/api/activities/{id}/signups/{signupId}/checkin` | POST | 签到 |

### 审批

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/approvals` | GET | 审批分页列表 |
| `/api/approvals/{id}` | GET | 审批详情 |
| `/api/approvals/{id}/histories` | GET | 审批历史 |
| `/api/approvals/{id}/approve` | POST | 审批通过 |
| `/api/approvals/{id}/reject` | POST | 审批驳回 |
| `/api/approvals/{id}/archive` | POST | 归档审批记录 |

### 招新与申请

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/recruitments` | GET | 招新计划列表 |
| `/api/recruitments` | POST | 创建招新计划 |
| `/api/applications` | POST | 提交入社申请 |
| `/api/applications/{id}/approve` | POST | 审批入社申请 |
| `/api/applications/{id}/reject` | POST | 驳回入社申请 |

## 测试与构建

### 后端测试

```bash
cd /Users/yzh/Desktop/Documents/软件工程\ jit/社团管理系统/club_final/club-backend
JWT_SECRET='01234567890123456789012345678901' mvn test
```

当前已覆盖的重点回归方向包括：

- JWT 配置校验
- 活动创建校验
- 审批步骤推进
- 审批权限边界
- 用户返回脱敏

### 前端构建

```bash
cd /Users/yzh/Desktop/Documents/软件工程\ jit/社团管理系统/club_final/club-management
npm run build
```

### CI

已配置 GitHub Actions：

[ci.yml](/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/.github/workflows/ci.yml)

会自动执行：

- 后端 `mvn test`
- 前端 `npm run build`

## 当前已知说明

- 前端生产包里 `request` 相关 chunk 仍然偏大，后续可以继续拆包优化。
- 前端当前仍使用 `sessionStorage` 保存 token；如果要进一步提升生产安全性，可以继续演进到 `HttpOnly Cookie + CSRF` 方案。
- 项目更适合课程设计、内部演示和迭代开发；若要正式长期上线，仍建议继续补充监控、集成测试和发布流程。

## 参考文件

- [SecurityConfig.java](/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/club-backend/src/main/java/com/club/config/SecurityConfig.java:1)
- [ApprovalController.java](/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/club-backend/src/main/java/com/club/controller/ApprovalController.java:1)
- [ActivityController.java](/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/club-backend/src/main/java/com/club/controller/ActivityController.java:1)
- [schema.sql](/Users/yzh/Desktop/Documents/软件工程 jit/社团管理系统/club_final/club-backend/src/main/resources/db/schema.sql:1)
