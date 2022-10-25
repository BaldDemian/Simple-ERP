create table `voucher` (
	`id` int not null auto_increment primary key comment '自增id',
	`customer_id` int not null comment '持有者的id',
	`value` decimal not null comment '面值',
	`state` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单据状态',
	`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注'
);
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 500, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 1000, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 1200, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 1400, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 2000, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 2500, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 3000, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 500, "test", "已使用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 500, "test", "不可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 300, "test", "可用");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(1, 1000, "test", "可用");

alter table `sale_sheet` add `voucher_id` int;
