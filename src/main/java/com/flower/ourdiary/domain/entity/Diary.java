package com.flower.ourdiary.domain.entity;

/*CREATE TABLE `diary` (
  `seq` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_seq` int(10) unsigned NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` json DEFAULT NULL,
  `share_state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0: not share\n',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Getter
@Setter
public class Diary {
    private Long seq;
    @JsonIgnore
    private Integer userSeq;
    private String title;
    private Content content;
    private Integer likeCount;
    private Integer shareState;
    private Date wantedDt;
    private Date createdAt;
    private Date updatedAt;

    private User user;
    private List<User> friendList;
    private List<Group> groupList;
    @JsonIgnore
    private TreeSet<DiaryPlace> placeList;
    @JsonIgnore
    private TreeSet<DiaryPicture> pictureList;
    private Boolean isLike;
    @JsonIgnore
    private Boolean isForbidden;

    public List<String> getPlaceNameList() {
        return placeList == null ? null
                : placeList.stream().map(DiaryPlace::getName).collect(Collectors.toList());
    }

    public List<String> getPictureUrlList() {
        return pictureList == null ? null
                : pictureList.stream().map(DiaryPicture::getPath).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class Content {
        private String text;
    }
}
