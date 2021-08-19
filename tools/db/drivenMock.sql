-- Adminer 4.8.0 MySQL 8.0.3-rc-log dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

CREATE DATABASE `drivenMock` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `drivenMock`;

CREATE TABLE `auditor`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `username`    varchar(255)    DEFAULT NULL,
    `status`      varchar(255)    DEFAULT NULL,
    `method`      varchar(255)    DEFAULT NULL,
    `url`         varchar(255)    DEFAULT NULL,
    `created_by`  varchar(20)     DEFAULT 'system',
    `created_at`  timestamp  NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by` varchar(20)     DEFAULT 'system',
    `modified_at` timestamp  NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `auditor` (`id`, `username`, `status`, `method`, `url`, `created_by`, `created_at`, `modified_by`, `modified_at`)
VALUES (1, 'admin', '200', 'POST', '/api/v1/dashboard/auth/signin', 'system', '2021-05-31 10:35:55', 'system', '2021-05-31 10:35:55');

CREATE TABLE `user`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `username`    varchar(255) NOT NULL,
    `email`       varchar(255) NOT NULL,
    `password`    varchar(255) NOT NULL,
    `session`     varchar(255)      DEFAULT NULL,
    `created_by`  varchar(20)       DEFAULT 'system',
    `created_at`  timestamp    NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by` varchar(20)       DEFAULT 'system',
    `modified_at` timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `user` (`id`, `username`, `email`, `password`, `session`, `created_by`, `created_at`, `modified_by`, `modified_at`)
VALUES (1, 'admin', 'admin@email.com', '$2a$12$GIQQ5AxMjXKt8EAqgs8Npe.5sBK1u4rcQSGBOwcAOUjCopG52EGjW', '', 'system', '2021-05-06 06:25:41', 'system', '2021-06-03 08:30:51'),
       (2, 'client', 'client@email.com', '$2a$12$YAWMNp.TUuWr.6SpUoc2pu5hApu/WHhhqRdzzo6BnfKGs5bG8YxNq', '', 'system', '2021-05-06 06:25:41', 'system', '2021-05-21 14:13:47'),
       (3, 'jhon', 'jhon@email.com', '$2a$12$d4cTD4DoG.5r9/1V0kRTH.pk4o/RgB9d5S7PFXEtiXy6BJCh8Vw.G', '', 'system', '2021-05-06 06:28:21', 'system', '2021-06-01 07:24:24'),
       (4, 'doe', 'doe@email.com', '$2a$12$xd2gy592FwcKX81OV0owVOnUzBdblzpmWJdQH9cQwJoKlPA7mUCoq', '', 'system', '2021-05-21 03:21:18', 'system', '2021-05-21 14:16:00');

CREATE TABLE `user_roles`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`     bigint(20) NOT NULL,
    `roles`       bigint(20) NOT NULL,
    `created_by`  varchar(20)     DEFAULT 'system',
    `created_at`  timestamp  NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by` varchar(20)     DEFAULT 'system',
    `modified_at` timestamp  NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `user_id` (`user_id`),
    CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `user_roles` (`id`, `user_id`, `roles`, `created_by`, `created_at`, `modified_by`, `modified_at`)
VALUES (9, 1, 0, 'system', '2021-05-06 06:25:41', 'system', '2021-05-06 06:25:41'),
       (10, 2, 1, 'system', '2021-05-06 06:25:41', 'system', '2021-05-06 06:25:41'),
       (11, 2, 2, 'system', '2021-05-06 06:25:41', 'system', '2021-05-06 06:25:41'),
       (12, 3, 0, 'system', '2021-05-06 06:28:21', 'system', '2021-05-06 06:28:21'),
       (13, 3, 1, 'system', '2021-05-06 06:28:21', 'system', '2021-05-06 06:28:21'),
       (14, 4, 0, 'system', '2021-05-21 03:21:18', 'system', '2021-05-21 03:21:18'),
       (15, 4, 1, 'system', '2021-05-21 03:21:18', 'system', '2021-05-21 03:21:18');

-- 2021-06-04 02:27:48