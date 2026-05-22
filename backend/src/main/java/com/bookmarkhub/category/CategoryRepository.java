package com.bookmarkhub.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByTeamIdOrderByIdAsc(Long teamId);

    Optional<Category> findByIdAndTeamId(Long id, Long teamId);

    long countByTeamId(Long teamId);
}
