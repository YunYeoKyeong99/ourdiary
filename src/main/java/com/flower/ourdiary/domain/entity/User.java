package com.flower.ourdiary.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

//CREATE TABLE `user` (
//    `seq` int(10) unsigned NOT NULL AUTO_INCREMENT,
//    `mail` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
//    `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
//    `nick` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
//    `self_introduction` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
//    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
//    `updated_at` datetime NOT NULL,
//    PRIMARY KEY (`seq`)
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer seq;
    private String email;
    private String name;
    private String nick;
    private Date createdAt;

    //가공한 부분
    private Boolean isFriend;
}
