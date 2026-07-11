package com.bookmarkhub.member.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberVO {

    private Long id;
    private String username;
    private String displayName;
    private String role;
}
