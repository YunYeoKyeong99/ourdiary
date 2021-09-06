package com.flower.ourdiary.domain.entity;

/*CREATE TABLE `user_auth` (
        `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `type` tinyint(3) unsigned NOT NULL COMMENT '1. id pw\n2. google\n3. naver\n4. kakao',
        `id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
        `pw` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `updated_at` datetime NOT NULL,
        `user_seq` int(10) unsigned NOT NULL,
        PRIMARY KEY (`seq`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/

import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserAuth {
    private Long seq;
    private UserAuthType type;
    private String id;
    private String pw;
    private Date createdAt;
    private Date updatedAt;
    private Integer userSeq;
}
