DROP TABLE IF EXISTS `tb_logistics`;
CREATE TABLE `tb_logistics`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `num`          varchar(255)   DEFAULT NULL COMMENT '物流编号',
    `address`      varchar(500)   DEFAULT NULL COMMENT '送货地址',
    `userid`       varchar(32)    DEFAULT NULL COMMENT '用户ID',
    `paytype`      varchar(512)   DEFAULT NULL COMMENT '支付类型',
    `price`        decimal(16, 2) DEFAULT NULL COMMENT '物流价格',
    `photo`        varchar(1024)  DEFAULT NULL COMMENT '物流图片',
    `addtime`      datetime       DEFAULT NULL COMMENT '发货时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='物流管理';

DROP TABLE IF EXISTS `tb_car`;
CREATE TABLE `tb_car`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `num`          varchar(255)   DEFAULT NULL COMMENT '车辆编号',
    `name`         varchar(255)   DEFAULT NULL COMMENT '车辆名称',
    `type`         varchar(255)   DEFAULT NULL COMMENT '车辆类型',
    `logistics_id`      varchar(32)    NOT NULL COMMENT '物流ID',
    `price`        decimal(16, 2) DEFAULT NULL COMMENT '车辆价格',
    `photo`        varchar(1024)  DEFAULT NULL COMMENT '车辆图片',
    `addtime`      datetime       DEFAULT NULL COMMENT '注册时间',
    `userid`       varchar(32)    DEFAULT NULL COMMENT '用户ID',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8 COMMENT='车辆管理';

DROP TABLE IF EXISTS `tb_cargo`;
CREATE TABLE `tb_cargo`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `num`          varchar(255)   DEFAULT NULL COMMENT '货物编号',
    `remark`       varchar(500)   DEFAULT NULL COMMENT '备注',
    `price`        decimal(16, 2) DEFAULT NULL COMMENT '货物价格',
    `logistics_id` varchar(32)    NOT NULL COMMENT '物流ID',
    `photo`        varchar(512)   DEFAULT NULL COMMENT '货物图片',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='货物管理';


DROP TABLE IF EXISTS `tb_courier`;
CREATE TABLE `tb_courier`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `num`          varchar(255)   DEFAULT NULL COMMENT '快递员编号',
    `remark`       varchar(500)   DEFAULT NULL COMMENT '备注',
    `price`        decimal(16, 2) DEFAULT NULL COMMENT '快递员工资',
    `logistics_id`      varchar(32)    NOT NULL COMMENT '物流ID',
    `photo`        varchar(512)   DEFAULT NULL COMMENT '快递员照片',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='快递员管理';


DROP TABLE IF EXISTS `tb_line`;
CREATE TABLE `tb_line`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `num`          varchar(255)   DEFAULT NULL COMMENT '线路编号',
    `remark`       varchar(500)   DEFAULT NULL COMMENT '备注',
    `logistics_id`      varchar(32)    NOT NULL COMMENT '物流ID',
    `photo`        varchar(512)   DEFAULT NULL COMMENT '线路图片',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='线路管理';


DROP TABLE IF EXISTS `tb_plan`;
CREATE TABLE `tb_plan`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `num`          varchar(255)   DEFAULT NULL COMMENT '规划编号',
    `remark`       varchar(500)   DEFAULT NULL COMMENT '备注',
    `price`        decimal(16, 2) DEFAULT NULL COMMENT '规划价格',
    `logistics_id`      varchar(32)    NOT NULL COMMENT '物流ID',
    `photo`        varchar(512)   DEFAULT NULL COMMENT '规划照片',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='规划管理';


DROP TABLE IF EXISTS `tb_file`;
CREATE TABLE `tb_file`
(
    `id`           varchar(32)    NOT NULL,
    `tenant_id`    varchar(32) DEFAULT NULL COMMENT '所属租户',
    `status`       char(1)     DEFAULT '0' COMMENT '状态',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标识',
    `created_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `created_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `updated_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `upload_type`          varchar(255)   DEFAULT NULL COMMENT '上传类型',
    `address`       varchar(500)   DEFAULT NULL COMMENT '存储地址',
    `file_name`      varchar(32)    NOT NULL COMMENT '文件名称',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='文件管理';