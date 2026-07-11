package com.bookmarkhub.member.service;

import com.bookmarkhub.member.dto.CreateTeamMemberRequest;
import com.bookmarkhub.member.vo.TeamMemberVO;
import com.bookmarkhub.shared.PageResponse;

public interface MemberService {

    TeamMemberVO create(String username, CreateTeamMemberRequest request);

    PageResponse<TeamMemberVO> list(String username);
}
