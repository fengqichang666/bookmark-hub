package com.bookmarkhub.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeamMemberRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String displayName;

    @NotBlank
    private String role;
}
