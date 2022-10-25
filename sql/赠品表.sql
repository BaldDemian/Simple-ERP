CREATE TABLE `gift_sheet` (
  `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '赠品单单据编号（格式为：ZPD-yyyyMMdd-xxxxx',
  `sale_sheet_id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联的销售单id',
  `supplier` int DEFAULT NULL COMMENT '供应商',
  `operator` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作员',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单据状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `salesman` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务员',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=dynamic;

CREATE TABLE `gift_sheet_content` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gift_sheet_id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '赠品单id',
  `pid` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品id',
  `quantity` int DEFAULT NULL COMMENT '数量',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=dynamic;