package com.bookmarkhub.auth;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findFirstByUserIdOrderByIdAsc(Long userId);

    Optional<TeamMember> findByIdAndTeamId(Long id, Long teamId);

    List<TeamMember> findByTeamIdOrderByIdAsc(Long teamId);

    long countByTeamId(Long teamId);
}
