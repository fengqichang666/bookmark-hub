package com.bookmarkhub.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserVO {

    private String username;
    private String displayName;
    private String role;
    private Long teamId;
}
