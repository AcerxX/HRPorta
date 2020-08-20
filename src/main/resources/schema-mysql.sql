CREATE TABLE IF NOT EXISTS `user`
(
    `id`                 int(11) unsigned              NOT NULL AUTO_INCREMENT,
    `username`           varchar(50) COLLATE utf8_bin  NOT NULL,
    `password`           varchar(100) COLLATE utf8_bin NOT NULL,
    `email`              varchar(100) COLLATE utf8_bin DEFAULT NULL,
    `full_name`          varchar(50) COLLATE utf8_bin  NOT NULL,
    `status`             tinyint(1) unsigned           DEFAULT '1',
    `created`            datetime                      DEFAULT CURRENT_TIMESTAMP,
    `modified`           datetime                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `u_username` (`username`),
    UNIQUE INDEX `i_email` (`email`),
    INDEX `status` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1;


CREATE TABLE IF NOT EXISTS `permission`
(
    `id`       int(11) unsigned NOT NULL AUTO_INCREMENT,
    `code`     varchar(50)      NOT NULL,
    `group`    varchar(50)      NOT NULL,
    `created`  datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified` datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `user_permission`
(
    `user_id`       int(11) unsigned NOT NULL,
    `permission_id` int(11) unsigned NOT NULL,
    UNIQUE INDEX `user_id` (`user_id`, `permission_id`),
    KEY `FK_user_permission_permission` (`permission_id`),
    KEY `FK_user_permission_user` (`user_id`)
) ENGINE = InnoDB;
