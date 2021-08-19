-- Adminer 4.8.0 MySQL 8.0.3-rc-log dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

CREATE DATABASE `drivenPayoptOne` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `drivenPayoptOne`;

CREATE TABLE `payopt_currencies`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `code`        varchar(255) NOT NULL,
    `name`        varchar(255) NOT NULL,
    `status`      tinyint(8)   NOT NULL,
    `created_by`  varchar(20)       DEFAULT 'system',
    `created_at`  timestamp    NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by` varchar(20)       DEFAULT 'system',
    `modified_at` timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `payopt_currencies` (`id`, `code`, `name`, `status`, `created_by`, `created_at`, `modified_by`, `modified_at`)
VALUES (1, 'IDR', 'Indonesia Rupiah', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (2, 'USD', 'US Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (3, 'MYR', 'Malaysia Ringgit', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (4, 'VND', 'Vietnamese Dong', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (5, 'SGD', 'Singapore Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (6, 'TWD', 'Taiwanese NT Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (7, 'THB', 'Thailand Baht', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (8, 'AUD', 'Australian Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (9, 'BDT', 'Bangladesh Taka', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (10, 'BND', 'Brunei Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (11, 'CNY', 'Chinese Renminbi Yuan', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (12, 'EUR', 'Euro', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (13, 'GBP', 'British Pound', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (14, 'HKD', 'Hong Kong Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (15, 'INR', 'Indian Rupee', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (16, 'JPY', 'Japanese Yen', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (17, 'KRW', 'South Korean Won', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (18, 'MOP', 'Macau Pataca', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (19, 'NZD', 'New Zealand Dollar', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (20, 'PHP', 'Philippines Peso', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44');

CREATE TABLE `payopt_platforms`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `code`        varchar(255) NOT NULL,
    `name`        varchar(255) NOT NULL,
    `status`      tinyint(8)   NOT NULL,
    `created_by`  varchar(20)       DEFAULT 'system',
    `created_at`  timestamp    NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by` varchar(20)       DEFAULT 'system',
    `modified_at` timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `payopt_platforms` (`id`, `code`, `name`, `status`, `created_by`, `created_at`, `modified_by`, `modified_at`)
VALUES (1, 'web', 'web application', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (2, 'mobile', 'mobile web application', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (3, 'ios', 'ios application ', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (4, 'android', 'android application', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (5, 'wechat', 'wechat application', 1, 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44');

CREATE TABLE `payopt_types`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `type`        varchar(255) NOT NULL,
    `name`        varchar(255) NOT NULL,
    `value`       varchar(255) NOT NULL,
    `created_by`  varchar(20)       DEFAULT 'system',
    `created_at`  timestamp    NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by` varchar(20)       DEFAULT 'system',
    `modified_at` timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `payopt_types` (`id`, `type`, `name`, `value`, `created_by`, `created_at`, `modified_by`, `modified_at`)
VALUES (1, 'category', 'creditcard', 'Credit Card', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (2, 'category', 'directdebit', 'Internet Banking', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (3, 'category', 'wallet', 'Wallet', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (4, 'category', 'unionpay', 'UnionPay', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (5, 'category', 'virtualaccount', 'Virtual Account', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (6, 'category', 'installment', 'Installment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (7, 'category', 'otc', 'Counter Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (8, 'category', 'atmmobilebanking', 'ATM / Mobile Banking', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (9, 'category', 'netbanking', 'Net Banking', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (10, 'category', 'paypal', 'Paypal', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (11, 'category', 'banktransfer', 'Bank Transfer', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (12, 'category', 'partner', 'Payment Partner', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (13, 'category', 'cod', 'Cash On Delivery', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (14, 'category', 'bigpay', 'Bigpay', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (15, 'category', 'rupay', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (16, 'category', 'myanmarpayment', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (17, 'category', 'paylater', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (18, 'category', 'kakaopay', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (19, 'category', 'payco', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (20, 'category', 'rakutenpay', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (21, 'category', 'other', 'Other Payment', 'system', '2020-06-10 13:23:44', 'system', '2020-06-10 13:23:44'),
       (22, 'category', 'group', 'group', 'system', '2020-06-10 13:23:44', 'system', '2020-12-23 10:32:35');

-- 2021-06-04 02:30:58