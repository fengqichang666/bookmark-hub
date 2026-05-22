package com.bookmarkhub.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsernameAndStatus(String username, String status);

    boolean existsByUsername(String username);
}
