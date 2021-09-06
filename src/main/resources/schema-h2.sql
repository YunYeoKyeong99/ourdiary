DROP TABLE IF EXISTS `remember_me_token`;
DROP TABLE IF EXISTS `user_fcm`;
DROP TABLE IF EXISTS `email_auth`;
DROP TABLE IF EXISTS `user_auth`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `group_member`;
DROP TABLE IF EXISTS `group`;
DROP TABLE IF EXISTS `friend_request`;
DROP TABLE IF EXISTS `friend_link`;
DROP TABLE IF EXISTS `friend`;
DROP TABLE IF EXISTS `diary_place`;
DROP TABLE IF EXISTS `diary_picture`;
DROP TABLE IF EXISTS `diary_group_tag`;
DROP TABLE IF EXISTS `diary_friend_tag`;
DROP TABLE IF EXISTS `diary_like`;
DROP TABLE IF EXISTS `diary`;
DROP TABLE IF EXISTS `SPRING_SESSION_ATTRIBUTES`;
DROP TABLE IF EXISTS `SPRING_SESSION`;

CREATE TABLE `SPRING_SESSION` (
	`PRIMARY_ID` CHAR(36) NOT NULL,
	`SESSION_ID` CHAR(36) NOT NULL,
	`CREATION_TIME` BIGINT NOT NULL,
	`LAST_ACCESS_TIME` BIGINT NOT NULL,
	`MAX_INACTIVE_INTERVAL` INT NOT NULL,
	`EXPIRY_TIME` BIGINT NOT NULL,
	`PRINCIPAL_NAME` VARCHAR(100),
	CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE UNIQUE INDEX `SPRING_SESSION_IX1` ON `SPRING_SESSION` (`SESSION_ID`);
CREATE INDEX `SPRING_SESSION_IX2` ON `SPRING_SESSION` (`EXPIRY_TIME`);
CREATE INDEX `SPRING_SESSION_IX3` ON `SPRING_SESSION` (`PRINCIPAL_NAME`);

CREATE TABLE `SPRING_SESSION_ATTRIBUTES` (
	`SESSION_PRIMARY_ID` CHAR(36) NOT NULL,
	`ATTRIBUTE_NAME` VARCHAR(200) NOT NULL,
	`ATTRIBUTE_BYTES` BLOB NOT NULL,
	CONSTRAINT `SPRING_SESSION_ATTRIBUTES_PK` PRIMARY KEY (`SESSION_PRIMARY_ID`, `ATTRIBUTE_NAME`),
	CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `SPRING_SESSION`(`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE TABLE `diary` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_seq` int(10) unsigned NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` json DEFAULT NULL,
  `like_count` int(10) unsigned NOT NULL DEFAULT 0,
  `share_state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0: not share\n',
  `wanted_dt` datetime NOT NULL,
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0: visible , 1: delete\n',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `diary_ix1` ON `diary` (`created_at`);
  -- where created_at 빠르게 조회
CREATE INDEX `diary_ix2` ON `diary` (`user_seq`);
  -- where user_seq 빠르게 조회

CREATE TABLE `diary_like` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `diary_seq` bigint(20) unsigned NOT NULL,
  `user_seq` int(10) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `diary_like_ix1` ON `diary_like` (`diary_seq`,`user_seq`);

CREATE TABLE `diary_friend_tag` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `diary_seq` bigint(20) unsigned NOT NULL,
  `friend_seq` int(10) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `diary_friend_tag_ix1` ON `diary_friend_tag` (`diary_seq`,`friend_seq`);
  -- where diary_seq 빠르게 조회

CREATE TABLE `diary_group_tag` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `diary_seq` bigint(20) unsigned NOT NULL,
  `group_seq` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `diary_group_tag_ix1` ON `diary_group_tag` (`diary_seq`,`group_seq`);
  -- where diary_seq 빠르게 조회

CREATE TABLE `diary_picture` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `diary_seq` bigint(20) unsigned NOT NULL,
  `path` varchar(255) NOT NULL,
  `turn` tinyint(3) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `diary_picture_ix1` ON `diary_picture` (`diary_seq`,`turn`);
  -- where diary_seq 빠르게 조회

CREATE TABLE `diary_place` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `diary_seq` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `turn` tinyint(4) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `diary_place_ix1` ON `diary_place` (`diary_seq`,`turn`);
  -- where diary_seq 빠르게 조회

CREATE TABLE `friend` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_seq` int(10) unsigned NOT NULL,
  `friend_seq` int(10) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `friend_ix1` ON `friend` (`user_seq`,`friend_seq`);
  -- user_seq, friend_seq 중복 추가 방지, where user_seq 빠르게 조회
CREATE INDEX `friend_ix2` ON `friend` (`friend_seq`);
  -- where friend_seq 빠르게 조회

CREATE TABLE `friend_link` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `friend_linkcol` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `friend_request` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `from_user_seq` int(10) unsigned NOT NULL,
  `to_user_seq` int(10) unsigned NOT NULL,
  `state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0: requested\n1: accepted\n2 : denied\n3: canceled',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `friend_request_ix1` ON `friend_request` (`to_user_seq`,`state`,`created_at`);
  -- where to_user_seq and state 빠르게 조회
CREATE INDEX `friend_request_ix2` ON `friend_request` (`from_user_seq`,`state`,`created_at`);
  -- where from_user_seq and state 빠르게 조회

CREATE TABLE `group` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nick` varchar(45) DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  `king_user_seq` int(10) unsigned NOT NULL,
  `user_count` int(10) unsigned NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `group_ix1` ON `group` (`nick`);
  -- nick에 대해 unique (nick 중복방지)

CREATE TABLE `group_member` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_seq` bigint(20) unsigned NOT NULL,
  `user_seq` int(10) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `group_member_ix1` ON `group_member` (`group_seq`,`user_seq`);
  -- group_seq, user_seq에 대해 unique (group내에 중복 user_seq 방지)

CREATE TABLE `user` (
  `seq` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `nick` varchar(45) DEFAULT NULL,
  `self_introduction` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `user_ix1` ON `user` (`nick`);
  -- nick에 대해 unique (nick 중복방지)

CREATE TABLE `user_auth` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(3) unsigned NOT NULL COMMENT '1. email pw\n2. google\n3. naver\n4. kakao',
  `id` varchar(255) NOT NULL,
  `pw` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_seq` int(10) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `user_auth_ix1` ON `user_auth` (`type`, `id`);
  -- 1. type, id에 대해 unique (1개의 로그인 수단이 여러 row로 중복존재 방지)
  -- 2. where type and id (로그인 시도) 빠르게 조회
CREATE UNIQUE INDEX `user_auth_ix2` ON `user_auth` (`type`, `user_seq`);
  -- type, user_seq에 대해 unique (동일타입 계정 여러개 하나의 user_seq에 연결방지) (ex) 카카오계정 2개를 하나의 user_seq로 연결 못하도록)

CREATE TABLE `email_auth` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(3) unsigned NOT NULL COMMENT '1. join\n2. reset password',
  `user_seq` int(10) unsigned NOT NULL,
  `token` varchar(64) NOT NULL,
  `id` varchar(255) NOT NULL,
  `pw` varchar(255) NOT NULL,
  `used_dt` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX `email_auth_ix1` ON `email_auth` (`token`);
  -- 모든 이메일 인증 링크는 unique하도록
CREATE INDEX `email_auth_ix2` ON `email_auth` (`type`, `id`);
  -- where type and id 조건으로 조회시 빠르게 조회
CREATE INDEX `email_auth_ix3` ON `email_auth` (`used_dt`);
  -- 새벽에 where used_dt is not null(사용완료된 인증메일)을 빠르게 조회해 제거하기위해
CREATE INDEX `email_auth_ix4` ON `email_auth` (`created_at`);
  -- 새벽에 created_at이 오래된(만료되버린 인증메일)을 빠르게 조회해 제거하기위해

CREATE TABLE `user_fcm` (
  `user_seq` int(10) unsigned NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `remember_me_token` (
  `user_seq` int(10) unsigned NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;