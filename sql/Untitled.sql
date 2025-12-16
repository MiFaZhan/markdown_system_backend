DROP TABLE IF EXISTS `markdown_file`;

USE markdown_file;

CREATE TABLE `markdown_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `file_name` varchar(255) NOT NULL COMMENT 'Markdown 文件名',
  `markdown_content` longtext COMMENT 'Markdown 文件内容',
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP 
                 ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_file` (`user_id`, `file_name`),
  KEY `idx_user_update_time` (`user_id`, `deleted`, `update_time`)
) COMMENT='Markdown 文件表';