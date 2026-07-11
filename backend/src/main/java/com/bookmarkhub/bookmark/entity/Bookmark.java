package com.bookmarkhub.bookmark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("bookmark")
public class Bookmark {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("team_id")
    private Long teamId;

    @TableField("category_id")
    private Long categoryId;

    private String title;

    private String url;

    private String description;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
