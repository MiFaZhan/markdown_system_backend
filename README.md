# Markdown System Backend

## 项目简介

一个现代化的前后端分离 Markdown 文档管理平台后端服务，支持项目管理、文件树导航、实时编辑、图片上传和文档分享等功能。

## 核心功能

### 用户系统
- **用户注册与登录**：基于 JWT 的无状态认证
- **用户信息管理**：获取当前用户信息
- **用户管理**：管理员可管理所有用户（列表、更新、删除）
- **角色权限系统**：基于角色的访问控制（RBAC），支持 AOP 注解鉴权

### 项目管理
- 创建、查询、更新、删除项目
- 项目逻辑删除与回收站
- 用户级别的项目隔离
- 唯一项目名校验

### 节点管理
- 文件夹和文件的树形结构管理
- 节点的创建、重命名、移动、删除
- 回收站功能（逻辑删除）
- 唯一节点名校验（同父节点下）

### Markdown 内容管理
- Markdown 文件内容的存储和读取
- 内容版本号管理
- 内容更新时间追踪

### 图片上传
- 支持图片上传到本地文件系统
- 按项目和节点组织存储路径
- 返回可访问的图片 URL

### 文档分享
- 创建分享链接（支持项目、文件夹、文件三种类型）
- 分享密码保护
- 分享过期时间设置
- 分享列表管理
- 公开访问接口

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.5 | 核心框架 |
| Java | 17 | 开发语言 |
| MyBatis-Plus | 3.5.7 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| JWT (jjwt) | 0.12.6 | 认证授权 |
| Spring Security Crypto | - | 密码加密 |
| MapStruct | 1.5.5 | 对象转换 |
| Lombok | - | 代码简化 |
| Spring AOP | - | 切面编程（权限校验） |
| Validation | - | 参数校验 |

## 项目结构

```
src/main/java/com/mifazhan/
├── annotation/           # 自定义注解
│   └── RequirePermission.java
├── aspect/               # AOP 切面
│   └── PermissionAspect.java
├── config/               # 配置类
│   ├── CorsConfig.java
│   ├── CorsProperties.java
│   ├── FileUploadConfig.java
│   ├── MyMetaObjectHandler.java
│   ├── MybatisPlusConfig.java
│   └── WebMvcConfig.java
├── controller/           # 控制器层
│   ├── ImageController.java
│   ├── MarkdownContentController.java
│   ├── NodeController.java
│   ├── ProjectController.java
│   ├── RoleController.java
│   ├── ShareLinkController.java
│   └── UserController.java
├── domain/               # 领域模型
│   ├── convert/          # MapStruct 转换器
│   ├── dto/              # 数据传输对象
│   ├── entity/           # 实体类
│   ├── exception/        # 异常处理
│   └── vo/               # 视图对象
├── mapper/               # 数据访问层
├── service/              # 业务逻辑层
│   ├── helper/           # 辅助类
│   └── impl/             # 实现类
└── util/                 # 工具类
    ├── JwtUtil.java
    └── UserContext.java
```

## 数据库设计

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| role | 角色表 |
| permission | 权限表 |
| role_permission | 角色权限关联表 |
| project | 项目表 |
| node | 节点表（文件树） |
| markdown_content | Markdown 内容表 |
| share_link | 分享链接表 |

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven

### 初始化数据库

```bash
mysql -u root -p < sql/markdown_system.sql
```

默认管理员账号：
- 用户名：`admin`
- 密码：`admin123`

### 配置应用

编辑 `src/main/resources/application.yml`，配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/markdown_system
    username: root
    password: your_password
```

### 运行项目

```bash
# 使用 Maven
./mvnw spring-boot:run

# 或直接运行主类
MarkdownSystemApplication.java
```

服务启动后访问：`http://localhost:8080`

## API 接口

### 用户接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录 | 否 |
| GET | `/api/user/info` | 获取当前用户信息 | 是 |
| GET | `/api/user/list` | 用户列表 | 是（需 user:manage 权限） |
| PUT | `/api/user` | 更新用户 | 是（需 user:manage 权限） |
| DELETE | `/api/user/{userId}` | 删除用户 | 是（需 user:manage 权限） |

### 项目接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/project` | 项目列表 | 是 |
| POST | `/api/project` | 创建项目 | 是 |
| PUT | `/api/project` | 更新项目 | 是 |
| DELETE | `/api/project/{projectId}` | 删除项目 | 是 |
| POST | `/api/project/restore` | 恢复项目 | 是 |

### 节点接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/node/tree` | 获取文件树 | 是 |
| POST | `/api/node` | 创建节点 | 是 |
| PUT | `/api/node` | 更新节点 | 是 |
| DELETE | `/api/node/{nodeId}` | 删除节点 | 是 |
| POST | `/api/node/upload` | 上传文件 | 是 |

### 内容接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/content/{nodeId}` | 获取内容 | 是 |
| PUT | `/api/content/{nodeId}` | 更新内容 | 是 |

### 图片接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/image/upload` | 上传图片 | 是 |

### 分享接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/share` | 创建分享 | 是 |
| PUT | `/api/share` | 更新分享 | 是 |
| GET | `/api/share/list` | 我的分享列表 | 是 |
| DELETE | `/api/share/{shareId}` | 删除分享 | 是 |
| POST | `/api/share/public/{shareCode}` | 访问分享 | 否 |
| GET | `/api/share/public/{shareCode}/content` | 获取分享内容 | 否 |
| GET | `/api/share/public/{shareCode}/file/{nodeId}` | 获取分享文件 | 否 |


