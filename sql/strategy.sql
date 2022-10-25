create table `level_discount_st`(
	id int not null auto_increment primary key,
	`level` int not null unique,
	`discount` decimal(3,2) not null
);
insert into level_discount_st(`level`, `discount`) values(1, 1), (2, 0.99), (3, 0.95), (4, 0.9), (5, 0.8);

insert into `customer`(`type`,`level`,name,phone,address,zipcode,email,line_of_credit,operator)
values("销售商","5","zhangsan","12345","nju","123456","sf@fea.com",20000000,"uncln");

create table `total_amount_st`(
	id int not null auto_increment primary key,
	`amount` decimal(10,2) not null unique,
	`voucher` decimal(10,2) default null,
	`state` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci default '可用'
);
insert into total_amount_st (amount, voucher) values(20000, 1000);
insert into total_amount_st(amount, voucher) values(2000, 200);
create table `total_amount_st_gift`(
	id int not null auto_increment primary key,
	st_id int not null comment "对应的总价赠送策略id",
	pid char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类id(11位) + 位置(5位) = 编号',
	quantity int not null default 1
);
insert into total_amount_st_gift(st_id, pid) values (1, "0000000000400000") comment "满20000送一台戴尔电脑";
insert into total_amount_st_gift(st_id, pid) values (1, "0000000000400001");