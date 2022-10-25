create table `voucher` (
	`id` int not null auto_increment primary key comment '����id',
	`customer_id` int not null comment '�����ߵ�id',
	`value` decimal not null comment '��ֵ',
	`state` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '����״̬',
	`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '��ע'
);
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 500, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 200, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 1000, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 1200, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 1400, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 2000, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 2500, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 3000, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 500, "test", "��ʹ��");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 500, "test", "������");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(2, 300, "test", "����");
insert into `voucher`(`customer_id`, `value`, `remark`, `state`) values(1, 1000, "test", "����");

alter table `sale_sheet` add `voucher_id` int;
