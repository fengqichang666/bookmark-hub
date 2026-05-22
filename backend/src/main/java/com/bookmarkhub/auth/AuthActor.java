package com.bookmarkhub.auth;

public record AuthActor(UserAccount user, TeamMember membership) {

    private static final String ADMIN_ROLE = "ADMIN";

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
