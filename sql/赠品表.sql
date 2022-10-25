CREATE TABLE `gift_sheet` (
  `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '��Ʒ�����ݱ�ţ���ʽΪ��ZPD-yyyyMMdd-xxxxx',
  `sale_sheet_id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '���������۵�id',
  `supplier` int DEFAULT NULL COMMENT '��Ӧ��',
  `operator` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '����Ա',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '��ע',
  `state` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '����״̬',
  `create_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  `salesman` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ҵ��Ա',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=dynamic;

CREATE TABLE `gift_sheet_content` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '����id',
  `gift_sheet_id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '��Ʒ��id',
  `pid` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '��Ʒid',
  `quantity` int DEFAULT NULL COMMENT '����',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '��ע',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=dynamic;