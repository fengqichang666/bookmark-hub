package com.bookmarkhub.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.auth.entity.TeamMember;
import com.bookmarkhub.auth.mapper.TeamMemberMapper;
import com.bookmarkhub.auth.service.TeamMemberService;
import org.springframework.stereotype.Service;

@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements TeamMemberService {
}
