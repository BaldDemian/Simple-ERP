# 银行账户
DROP TABLE IF EXISTS `account`;
create table `account`(
    `name` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '账户名称' primary key,
    `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '账户金额'
);
# 转账列表（与收款单和付款单关联）
DROP TABLE IF EXISTS `transfer_content`;
create table `transfer_content`(
    `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '关联的收款单或付款单ID',
    `account` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '银行账户',
    `transfer_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '转账金额',
    `remark` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注'
);
# 收款单
DROP TABLE IF EXISTS `collect_sheet`;
create table `collect_sheet`(
    `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收款单ID',
    `customer` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '客户',
    `operator` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '操作员',
    `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额汇总',
    `state` varchar(31) collate utf8mb4_general_ci null comment '状态',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间'
);
# 付款单
DROP TABLE IF EXISTS `pay_sheet`;
create table `pay_sheet`(
    `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '付款单ID',
    `customer` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '客户',
    `operator` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '操作员',
    `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额汇总',
    `state` varchar(31) collate utf8mb4_general_ci null comment '状态',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间'
);
# 现金费用单
DROP TABLE IF EXISTS `cash_sheet`;
create table `cash_sheet`(
    `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '现金费用单ID',
    `account` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '银行账户',
    `operator` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '操作员',
    `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额汇总',
    `state` varchar(31) collate utf8mb4_general_ci null comment '状态',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间'
);
# 现金费用单的条目
DROP TABLE IF EXISTS `cash_sheet_content`
create table `cash_sheet_content`(
    `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '关联的现金费用单ID',
    `name` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '条目名',
    `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
    `remark` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注'
);
# 工资单
DROP TABLE IF EXISTS `salary_sheet`;
create table `salary_sheet`(
     `id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '工资单ID',
     `employee_id` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '员工编号',
     `name` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '员工姓名',
     `account` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '银行账户名',
     `raw_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '应发工资',
     `tax` decimal(10, 2) NULL DEFAULT NULL COMMENT '税款',
     `actual_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际工资',
     `state` varchar(31) collate utf8mb4_general_ci null comment '状态',
     `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间'
);