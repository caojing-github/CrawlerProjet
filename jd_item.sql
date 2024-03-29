CREATE TABLE `jd_item` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `spu` bigint(15) DEFAULT NULL COMMENT '大图id',
  `sku` bigint(15) DEFAULT NULL COMMENT '小图id',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `price` bigint(10) DEFAULT NULL COMMENT '价格',
  `pic` varchar(200) DEFAULT NULL COMMENT '图片',
  `url` varchar(200) DEFAULT NULL COMMENT '详情地址',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sku` (`sku`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='商品表';
