CREATE TABLE `markdown_node`  (
  `node_id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点ID',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父节点ID（NULL 表示项目根）',
  `node_type` tinyint NOT NULL COMMENT '节点类型 0文件夹 1文件',
  `node_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称（文件夹名或文件名）',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Markdown 内容（仅文件节点有效）',
  `sort_order` int NULL DEFAULT 0 COMMENT '同级排序',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号（仅文件）',
  `creation_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`node_id`) USING BTREE,
  UNIQUE INDEX `uk_node_name`(`project_id` ASC, `parent_id` ASC, `node_name` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_project_parent`(`project_id` ASC, `parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目内文件树节点表' ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `uk_user_project`(`user_id` ASC, `project_name` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_user_update_time`(`user_id` ASC, `deleted` ASC, `update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;


