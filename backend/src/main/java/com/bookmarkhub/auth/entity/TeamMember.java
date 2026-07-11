package com.bookmarkhub.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("team_member")
public class TeamMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("team_id")
    private Long teamId;

    @TableField("user_id")
    private Long userId;

    private String role;

    @TableField("joined_at")
    private LocalDateTime joinedAt;
}
