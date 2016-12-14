/*
SQLyog Community Edition- MySQL GUI v8.01 
MySQL - 5.7.13-0ubuntu0.16.04.2 : Database - famtree
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`famtree` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `famtree`;

/*Table structure for table `Audit` */

DROP TABLE IF EXISTS `Audit`;

CREATE TABLE `Audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `acttype` int(11) NOT NULL,
  `createtime` timestamp NULL DEFAULT NULL,
  `dowhat` text,
  `orgid` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKil3flgphhakua52ibprdlngiq` (`creator_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `Audit` */

/*Table structure for table `group_tbl` */

DROP TABLE IF EXISTS `group_tbl`;

CREATE TABLE `group_tbl` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `disabled` int(11) NOT NULL,
  `groupname` varchar(255) NOT NULL,
  `manager_mask` int(11) DEFAULT NULL,
  `user_mask` int(11) DEFAULT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `group_tbl` */

/*Table structure for table `group_user` */

DROP TABLE IF EXISTS `group_user`;

CREATE TABLE `group_user` (
  `user_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  KEY `FKo51f1ge6s6ecgo3b1h32v03bs` (`group_id`),
  KEY `FKdrsdxw31nr95w1bqculu89n6w` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `group_user` */

/*Table structure for table `individual` */

DROP TABLE IF EXISTS `individual`;

CREATE TABLE `individual` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `given_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `family_name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gender` tinyint(4) DEFAULT '1' COMMENT '1-male, 0-female',
  `alias` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `death` date DEFAULT NULL,
  `father_id` bigint(20) DEFAULT NULL,
  `mother_id` bigint(20) DEFAULT NULL,
  `pedigree_id` bigint(20) NOT NULL,
  `seq` tinyint(4) DEFAULT NULL,
  `gen` int(11) DEFAULT NULL COMMENT 'generation',
  PRIMARY KEY (`id`),
  KEY `father_id` (`father_id`),
  KEY `mother_id` (`mother_id`),
  KEY `pedigree_id` (`pedigree_id`)
) ENGINE=MyISAM AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `individual` */

insert  into `individual`(`id`,`given_name`,`family_name`,`gender`,`alias`,`birth`,`death`,`father_id`,`mother_id`,`pedigree_id`,`seq`,`gen`) values (102,'会','程',1,NULL,NULL,NULL,101,NULL,1,1,17),(101,'叔本','程',1,NULL,NULL,NULL,97,NULL,1,1,16),(100,'叔毕','程',1,NULL,NULL,NULL,96,NULL,1,1,16),(99,'伯恭','程',1,NULL,NULL,NULL,95,NULL,1,4,15),(98,'伯桃','程',1,NULL,NULL,NULL,95,NULL,1,3,15),(97,'伯先','程',1,NULL,NULL,NULL,95,NULL,1,2,15),(96,'伯丕','程',1,NULL,NULL,NULL,95,NULL,1,1,15),(95,'婴','程',1,NULL,NULL,NULL,94,NULL,1,1,14),(94,'德邈','程',1,NULL,NULL,NULL,93,NULL,1,1,13),(93,'思陵','程',1,NULL,NULL,NULL,92,NULL,1,1,12),(92,'抚宜','程',1,NULL,NULL,NULL,91,NULL,1,1,11),(91,'公龛','程',1,NULL,NULL,NULL,90,NULL,1,1,10),(90,'应祖','程',1,NULL,NULL,NULL,89,NULL,1,1,9),(89,'君识','程',1,NULL,NULL,NULL,88,NULL,1,1,8),(88,'黑肱','程',1,NULL,NULL,NULL,87,NULL,1,1,7),(87,'仲辛','程',1,NULL,NULL,NULL,86,NULL,1,1,6),(86,'休父','程',1,NULL,NULL,NULL,85,NULL,1,1,5),(85,'子臧','程',1,NULL,NULL,NULL,84,NULL,1,1,4),(84,'仲壬','程',1,NULL,NULL,NULL,83,NULL,1,1,3),(83,'廪丁','程',1,NULL,NULL,NULL,82,NULL,1,1,2),(82,'伯符','程',1,NULL,NULL,NULL,NULL,NULL,1,1,1),(103,'辨','程',1,NULL,NULL,NULL,102,NULL,1,1,18),(104,'括','程',1,NULL,NULL,NULL,102,NULL,1,2,18),(105,'繁','程',1,NULL,NULL,NULL,102,NULL,1,3,18),(106,'仁','程',1,NULL,NULL,NULL,103,NULL,1,1,19),(107,'叔虎','程',1,NULL,NULL,NULL,104,NULL,1,1,19),(108,'德才','程',1,NULL,NULL,NULL,106,NULL,1,1,20),(109,'景遂','程',1,NULL,NULL,NULL,107,NULL,1,1,20),(110,'逸','程',1,NULL,NULL,NULL,109,NULL,1,1,21),(111,'逷','程',1,NULL,NULL,NULL,109,NULL,1,2,21),(112,'述','程',1,NULL,NULL,NULL,109,NULL,1,3,21),(113,'远','程',1,NULL,NULL,NULL,109,NULL,1,4,21),(114,'彪','程',1,NULL,NULL,NULL,110,NULL,1,1,22),(115,'玮','程',1,NULL,NULL,NULL,113,NULL,1,1,22),(116,'邈','程',1,NULL,NULL,NULL,114,NULL,1,1,23),(117,'愿','程',1,NULL,NULL,NULL,115,NULL,1,1,23),(118,'慤','程',1,NULL,NULL,NULL,115,NULL,1,2,23),(119,'珍和','程',1,NULL,NULL,NULL,118,NULL,1,1,24),(120,'黑','程',1,NULL,NULL,NULL,119,NULL,1,1,25);

/*Table structure for table `individual_field` */

DROP TABLE IF EXISTS `individual_field`;

CREATE TABLE `individual_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_name` varchar(16) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `individual_field` */

insert  into `individual_field`(`id`,`field_name`) values (1,'出生地'),(2,'亡故地'),(3,'简介');

/*Table structure for table `individual_property` */

DROP TABLE IF EXISTS `individual_property`;

CREATE TABLE `individual_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `individual_id` bigint(20) NOT NULL,
  `field_id` bigint(20) NOT NULL,
  `field_value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `individual_id` (`individual_id`),
  KEY `field_id` (`field_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `individual_property` */

/*Table structure for table `individual_story` */

DROP TABLE IF EXISTS `individual_story`;

CREATE TABLE `individual_story` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `individual_id` bigint(20) NOT NULL,
  `story` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `individual_id` (`individual_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `individual_story` */

/*Table structure for table `pedigree` */

DROP TABLE IF EXISTS `pedigree`;

CREATE TABLE `pedigree` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pedigree_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `family_name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `root_individual_id` bigint(20) DEFAULT NULL,
  `create_time` date NOT NULL,
  `creator_id` bigint(20) NOT NULL,
  `private_generation` tinyint(4) DEFAULT '5' COMMENT '0:all public; >0: the private generation number(count from leaf)',
  PRIMARY KEY (`id`),
  KEY `creator_id` (`creator_id`),
  KEY `root_individual_id` (`root_individual_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `pedigree` */

insert  into `pedigree`(`id`,`pedigree_name`,`family_name`,`root_individual_id`,`create_time`,`creator_id`,`private_generation`) values (1,'Cheng test','程',NULL,'2016-12-12',1,5);

/*Table structure for table `pedigree_field` */

DROP TABLE IF EXISTS `pedigree_field`;

CREATE TABLE `pedigree_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pedigree_id` bigint(20) NOT NULL,
  `field_id` bigint(20) NOT NULL,
  `seq` tinyint(4) DEFAULT '1' COMMENT 'sequence',
  `print_title` tinyint(4) DEFAULT '0' COMMENT '0:print no name,1-print title',
  PRIMARY KEY (`id`),
  KEY `pedigree_id` (`pedigree_id`),
  KEY `field_id` (`field_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `pedigree_field` */

/*Table structure for table `pedigree_users` */

DROP TABLE IF EXISTS `pedigree_users`;

CREATE TABLE `pedigree_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `privilege` tinyint(4) DEFAULT '1' COMMENT '1:read, 3:read/write',
  `pedigree_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pedigree_id` (`pedigree_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `pedigree_users` */

/*Table structure for table `spouse` */

DROP TABLE IF EXISTS `spouse`;

CREATE TABLE `spouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `husband_id` bigint(20) NOT NULL,
  `wife_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `husband_id` (`husband_id`),
  KEY `wife_id` (`wife_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `spouse` */

/*Table structure for table `user_log` */

DROP TABLE IF EXISTS `user_log`;

CREATE TABLE `user_log` (
  `userlog_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `actiontime` datetime DEFAULT NULL,
  `dowhat` varchar(255) DEFAULT NULL,
  `info` varchar(255) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userlog_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `user_log` */

/*Table structure for table `user_tbl` */

DROP TABLE IF EXISTS `user_tbl`;

CREATE TABLE `user_tbl` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `disabled` int(11) DEFAULT NULL,
  `fullname` varchar(255) NOT NULL,
  `inused` int(11) DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `level` int(11) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `user_tbl` */

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `family_name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `given_name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `temp_pass` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'for forget password',
  `temppass_expire` datetime DEFAULT NULL,
  `disabled` tinyint(4) DEFAULT '0',
  `level` tinyint(4) DEFAULT '5',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`family_name`,`given_name`,`password`,`last_login`,`temp_pass`,`temppass_expire`,`disabled`,`level`) values (1,'colley888@gmail.com','Cheng','Chaokai','9f522b88b89d19428355a561af5281c2','2016-12-12 14:51:50',NULL,NULL,0,50);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;