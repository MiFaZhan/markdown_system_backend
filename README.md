# Markdown System Backend

## 🚀 项目未完工，正在开发中

## 📝 项目简介

一个现代化的前后端分离 Markdown 文档管理平台，支持项目管理、文件树导航和实时编辑功能。此仓库为后端代码仓库，前端仓库地址如下 [前端仓库](https://github.com/MiFaZhan/markdown_system_frontend)

## ✨ 核心功能

- **项目管理**：创建、查询、更新、删除项目
- **节点管理**：支持文件夹和文件的树形结构管理
- **Markdown 内容管理**：Markdown 文件内容的存储和版本控制
- **逻辑删除**：项目和节点支持逻辑删除，内容表采用物理删除

## 🛠️ 技术栈

- **核心框架**：Spring Boot 3.3.5
- **Java 版本**：JDK 17
- **构建工具**：Maven
- **ORM 框架**：MyBatis-Plus 3.5.7
- **数据库**：MySQL 8.0+
- **对象转换**：MapStruct 1.5.5
- **代码简化**：Lombok

## 📂 项目结构

```
markdown_system_backend/
├── src/main/
│   ├── java/com/mifazhan/
│   │   ├── config/              # 配置类
│   │   │   ├── CorsConfig.java
│   │   │   ├── MyMetaObjectHandler.java
│   │   │   └── MybatisPlusConfig.java
│   │   ├── controller/          # 控制器层
│   │   │   ├── NodeController.java
│   │   │   └── ProjectController.java
│   │   ├── domain/              # 领域模型
│   │   │   ├── convert/         # MapStruct 转换器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── exception/       # 异常处理
│   │   │   └── vo/              # 视图对象
│   │   ├── mapper/              # 数据访问层
│   │   │   ├── MarkdownContentMapper.java
│   │   │   ├── NodeMapper.java
│   │   │   └── ProjectMapper.java
│   │   └── service/             # 业务逻辑层
│   │       ├── impl/
│   │       ├── NodeService.java
│   │       └── ProjectService.java
│   └── resources/
│       ├── com/mifazhan/mapper/ # MyBatis XML 映射文件
│       ├── application.yml      # 应用配置
│       └── logback-spring.xml   # 日志配置
├── sql/
│   └── Markdown-System.sql      # 数据库初始化脚本
└── pom.xml                      # Maven 配置文件
```

## 📊 数据库设计

### 项目表 (project)
- 存储项目基本信息
- 支持用户级别的项目隔离
- 唯一索引：同一用户下项目名称不重复

### 节点表 (node)
- 支持树形结构（文件夹和文件）
- node_type: 0=文件夹，1=文件
- 唯一索引：同一父节点下子节点名称不重复

### Markdown 内容表 (markdown_content)
- 存储 Markdown 文件内容
- 支持版本号管理
- 物理删除策略（不使用逻辑删除）

## 🔧 配置说明

### MyBatis-Plus 配置
- 全局逻辑删除字段：`deleted`
- 逻辑删除值：1（已删除）
- 逻辑未删除值：0（未删除）

### 日志配置
- 控制台输出 SQL 语句
- 使用 Logback 进行日志管理

### 跨域配置
- 支持跨域请求（CorsConfig）
- 适配前后端分离架构

## 📝 开发说明

### 代码规范
- 使用 Lombok 简化 getter/setter 等样板代码
- 使用 MapStruct 进行对象转换
- Controller-Service-Mapper 三层架构
- 统一返回结果封装（Result）
- 全局异常处理（GlobalExceptionHandler）

### 特殊业务规则
- 当节点类型为 1（文件）时，删除节点需同步删除 markdown_content 表中的内容
- Markdown 内容表采用物理删除，不使用逻辑删除

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 [许可证名称] 许可证。

## 📧 联系方式

如有问题，请联系项目维护者。
