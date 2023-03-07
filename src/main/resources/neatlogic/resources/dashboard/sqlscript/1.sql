-- ----------------------------
-- Table structure for dashboard
-- ----------------------------
CREATE TABLE `dashboard` (
  `id` bigint NOT NULL COMMENT '唯一标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `is_active` tinyint(1) DEFAULT NULL COMMENT '是否激活',
  `type` enum('custom','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'custom' COMMENT '类型',
  `fcd` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
  `fcu` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `lcd` timestamp(3) NULL DEFAULT NULL COMMENT '最后修改时间',
  `lcu` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最后修改人',
  `widget_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '组件配置',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_fcu` (`fcu`) USING BTREE,
  KEY `idx_fcd` (`fcd`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表板表';

-- ----------------------------
-- Table structure for dashboard_authority
-- ----------------------------
CREATE TABLE `dashboard_authority` (
  `dashboard_id` bigint NOT NULL COMMENT '面板id',
  `type` enum('common','user','team','role') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '查看权限类型',
  `uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '查看权限uuid',
  PRIMARY KEY (`dashboard_id`,`type`,`uuid`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_uuid` (`uuid`) USING BTREE,
  KEY `idx_dashboard_uuid` (`dashboard_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表板权限表';

-- ----------------------------
-- Table structure for dashboard_default
-- ----------------------------
CREATE TABLE `dashboard_default` (
  `dashboard_id` bigint NOT NULL COMMENT '仪表板唯一标识',
  `user_uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '仪表板用户',
  `type` enum('custom','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'custom' COMMENT '仪表板类型',
  PRIMARY KEY (`dashboard_id`,`user_uuid`,`type`) USING BTREE,
  KEY `idx_uuid_userid_type` (`dashboard_id`,`user_uuid`,`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表板默认表';

-- ----------------------------
-- Table structure for dashboard_userdefault
-- ----------------------------
CREATE TABLE `dashboard_userdefault` (
  `dashboard_id` bigint NOT NULL COMMENT '仪表板唯一标识',
  `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
  PRIMARY KEY (`dashboard_id`,`user_uuid`) USING BTREE,
  UNIQUE KEY `uk_user_uuid` (`user_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户默认仪表板表';

-- ----------------------------
-- Table structure for dashboard_visitcounter
-- ----------------------------
CREATE TABLE `dashboard_visitcounter` (
  `dashboard_id` bigint NOT NULL COMMENT '仪表板id',
  `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
  `visit_count` int DEFAULT NULL COMMENT '访问次数',
  PRIMARY KEY (`dashboard_id`,`user_uuid`) USING BTREE,
  KEY `idx_user_uuid` (`user_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表板访问统计表';

-- ----------------------------
-- Table structure for dashboard_widget
-- ----------------------------
CREATE TABLE `dashboard_widget` (
  `uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一标识',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'porlet名称',
  `refresh_interval` int NOT NULL DEFAULT '1' COMMENT '自动刷新间隔，单位秒，0代表不刷新',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `dashboard_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '面板Id',
  `handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件',
  `chart_type` enum('barchart','piechart','stackbarchart','areachart','linechart','columnchart','stackcolumnchart','tablechart','numberchart','donutchart') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '视图模板',
  `condition_config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '条件配置',
  `chart_config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '图表配置',
  `detail_widget_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '明细组件uuid',
  `x` int DEFAULT NULL COMMENT 'x',
  `y` int DEFAULT NULL COMMENT 'y',
  `h` int DEFAULT NULL COMMENT 'h',
  `w` int DEFAULT NULL COMMENT 'w',
  `i` int DEFAULT NULL COMMENT 'i',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='dashboard_widget';