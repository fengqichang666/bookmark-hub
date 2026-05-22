package com.bookmarkhub.bookmark;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByTeamIdOrderByIdAsc(Long teamId);

    Optional<Bookmark> findByIdAndTeamId(Long id, Long teamId);

    long countByTeamId(Long teamId);
}
