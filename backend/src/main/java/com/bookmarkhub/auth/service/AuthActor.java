package com.bookmarkhub.auth.service;

import com.bookmarkhub.auth.entity.TeamMember;
import com.bookmarkhub.auth.entity.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 领域内部的"当前操作者"对象，聚合 UserAccount + TeamMember。
 * 不是 DTO/VO，只在 service 层内部流转。
 */
@Getter
@AllArgsConstructor
public class AuthActor {

    private static final String ADMIN_ROLE = "ADMIN";

    private final UserAccount user;
    private final TeamMember membership;

    public Long userId() {
        return user.getId();
    }

    public String username() {
        return user.getUsername();
    }

    public String displayName() {
        return user.getDisplayName();
    }

    public String status() {
        return user.getStatus();
    }

    public Long teamId() {
        return membership.getTeamId();
    }

    public String role() {
        return membership.getRole();
    }

    public boolean isAdmin() {
        return ADMIN_ROLE.equals(role());
    }
}
