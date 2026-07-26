package com.bookmarkhub.bookmark.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 字段名到列名的映射由 mybatis-plus.configuration.map-underscore-to-camel-case 完成，
 * 因此 teamId -> team_id 这类无需再写 @TableField。
 */
@Data
@TableName("bookmark")
public class Bookmark {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long categoryId;

    private String title;

    private String url;

    private String description;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记。有操作日志需要溯源，删除后仍要能查到原记录，因此不做物理删除。
     * removeById 会自动改写成 UPDATE ... SET deleted = 1，普通查询自动追加 deleted = 0。
     */
    @TableLogic
    private Integer deleted;
}
