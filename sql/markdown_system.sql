/*
 Navicat Premium Dump SQL

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : markdown_system

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 14/02/2026 16:39:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for markdown_content
-- ----------------------------
DROP TABLE IF EXISTS `markdown_content`;
CREATE TABLE `markdown_content`  (
  `node_id` bigint NOT NULL COMMENT '节点ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Markdown 内容',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
  `update_time` datetime NOT NULL COMMENT '内容更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  PRIMARY KEY (`node_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Markdown内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of markdown_content
-- ----------------------------

-- ----------------------------
-- Table structure for node
-- ----------------------------
DROP TABLE IF EXISTS `node`;
CREATE TABLE `node`  (
  `node_id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点ID',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父节点ID（NULL 表示项目根）',
  `node_type` tinyint NOT NULL COMMENT '节点类型：0=文件夹，1=文件',
  `node_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点名称',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  PRIMARY KEY (`node_id`) USING BTREE,
  UNIQUE INDEX uk_node_name_active (project_id, parent_id, node_name, (CASE WHEN deleted = 0 THEN 1 ELSE NULL END)),
  INDEX `idx_project_parent`(`project_id` ASC, `parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '节点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of node
-- ----------------------------

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `project_id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '?' COMMENT '图标',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`project_id`) USING BTREE,
  UNIQUE INDEX `uk_user_project_active`(`user_id`, `project_name`, (CASE WHEN deleted = 0 THEN 1 ELSE NULL END)),
  INDEX `idx_user_update_time`(`user_id` ASC, `deleted` ASC, `update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色代码（admin/user/guest）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '管理员', 'admin', '系统管理员，拥有最高权限', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role` VALUES (2, '用户', 'user', '普通用户，可以管理自己的项目', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role` VALUES (3, '游客', 'guest', '游客，只能查看不能修改', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role_id` bigint NULL DEFAULT 2 COMMENT '角色ID（默认为普通用户）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '启用状态：0=禁用，1=正常',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$LvkQMx.tCCspDvuQdSmOfei1ffnGgO/V8ifBndZt9zvqfSba3D1QW', 'admin123@markdown.com', 1, '系统管理员', 1, '2026-02-14 16:39:42', '2026-02-14 16:39:42', 0);

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `permission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限代码（如 user:read）',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`permission_id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, 'user:manage', '用户管理', '管理系统用户', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `permission` VALUES (2, 'project:*', '项目管理', '拥有项目的所有权限', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `permission` VALUES (3, 'node:*', '节点管理', '拥有节点的所有权限', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `permission` VALUES (4, 'content:*', '内容管理', '拥有内容的所有权限', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `permission` VALUES (5, 'project:read', '查看项目', '可以查看项目', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `permission` VALUES (6, 'node:read', '查看节点', '可以查看节点', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `permission` VALUES (7, 'content:read', '查看内容', '可以查看内容', '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 1, 1, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (2, 1, 2, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (3, 1, 3, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (4, 1, 4, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (5, 2, 2, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (6, 2, 3, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (7, 2, 4, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (8, 3, 5, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (9, 3, 6, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);
INSERT INTO `role_permission` VALUES (10, 3, 7, '2026-02-14 16:34:53', '2026-02-14 16:34:53', 0);

SET FOREIGN_KEY_CHECKS = 1;
