package com.bookmarkhub.member.service.impl;

import com.bookmarkhub.auth.entity.TeamMember;
import com.bookmarkhub.auth.entity.UserAccount;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.auth.service.TeamMemberService;
import com.bookmarkhub.auth.service.UserAccountService;
import com.bookmarkhub.member.dto.CreateTeamMemberRequest;
import com.bookmarkhub.member.service.MemberService;
import com.bookmarkhub.member.vo.TeamMemberVO;
import com.bookmarkhub.shared.BizException;
import com.bookmarkhub.shared.ErrorCode;
import com.bookmarkhub.shared.PageResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final TeamMemberService teamMemberService;
    private final UserAccountService userAccountService;
    private final AuthService authService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeamMemberVO create(String username, CreateTeamMemberRequest request) {
        AuthActor actor = authService.requireActor(username);
        boolean usernameTaken = userAccountService.lambdaQuery()
                .eq(UserAccount::getUsername, request.getUsername())
                .exists();
        if (usernameTaken) {
            throw new BizException(ErrorCode.USERNAME_TAKEN);
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setPasswordHash(authService.encodePassword(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getUsername() + "@bookmarkhub.local");
        user.setStatus(ACTIVE_STATUS);
        userAccountService.save(user);

        TeamMember member = new TeamMember();
        member.setTeamId(actor.teamId());
        member.setUserId(user.getId());
        member.setRole(request.getRole());
        teamMemberService.save(member);
        return toVO(member, user);
    }

    @Override
    public PageResponse<TeamMemberVO> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<TeamMember> members = teamMemberService.lambdaQuery()
                .eq(TeamMember::getTeamId, actor.teamId())
                .orderByAsc(TeamMember::getId)
                .list();
        if (members.isEmpty()) {
            return new PageResponse<>(List.of());
        }

        // 一次批量查出所有成员对应的用户，避免逐条 getById 造成 N+1
        Map<Long, UserAccount> usersById = userAccountService
                .listByIds(members.stream().map(TeamMember::getUserId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(UserAccount::getId, Function.identity()));

        List<TeamMemberVO> items = members.stream()
                .map(member -> {
                    UserAccount user = usersById.get(member.getUserId());
                    if (user == null) {
                        throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
                    }
                    return toVO(member, user);
                })
                .toList();
        return new PageResponse<>(items);
    }

    private TeamMemberVO toVO(TeamMember member, UserAccount user) {
        return new TeamMemberVO(member.getId(), user.getUsername(), user.getDisplayName(), member.getRole());
    }
}
