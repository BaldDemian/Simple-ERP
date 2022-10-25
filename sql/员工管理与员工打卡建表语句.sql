CREATE TABLE `worker` (
 `id` int NOT NULL AUTO_INCREMENT COMMENT '员工id',
 `name` varchar(20) NOT NULL COMMENT '员工姓名',
 `sex` int DEFAULT NULL COMMENT '性别',
 `birth` varchar(20) DEFAULT NULL COMMENT '生日',
 `phonenum` varchar(11) DEFAULT NULL COMMENT '手机号',
 `role` varchar(20) DEFAULT NULL COMMENT '角色',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb3;


CREATE TABLE `card` (
 `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
 `worker_id` int NOT NULL COMMENT '员工id',
 `card_num` int DEFAULT NULL COMMENT '打卡次数',
 `late_num` int DEFAULT NULL,
 `date` varchar(20) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;