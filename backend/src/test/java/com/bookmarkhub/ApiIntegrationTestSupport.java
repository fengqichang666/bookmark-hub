package com.bookmarkhub;

import com.bookmarkhub.auth.JwtTokenService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ApiIntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JwtTokenService jwtTokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void resetData() {
        deleteIfTableExists("import_record");
        deleteIfTableExists("bookmark");
        deleteIfTableExists("category");
        deleteIfTableExists("team_member");
        deleteIfTableExists("users");

        jdbcTemplate.update(
                """
                INSERT INTO users (id, username, password_hash, display_name, email, status, created_at, updated_at)
                VALUES (1, 'admin', ?, 'System Administrator', 'admin@bookmarkhub.local', 'ACTIVE', ?, ?)
                """,
                passwordEncoder.encode("Admin@123456"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        jdbcTemplate.update(
                "INSERT INTO team_member (id, team_id, user_id, role, joined_at) VALUES (1, 1, 1, 'ADMIN', ?)",
                LocalDateTime.now()
        );
    }

    protected void insertMember(long id, String username, String displayName, String role) {
        jdbcTemplate.update(
                """
                INSERT INTO users (id, username, password_hash, display_name, email, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?, ?)
                """,
                id,
                username,
                passwordEncoder.encode("Password@123"),
                displayName,
                username + "@bookmarkhub.local",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        jdbcTemplate.update(
                "INSERT INTO team_member (id, team_id, user_id, role, joined_at) VALUES (?, 1, ?, ?, ?)",
                id,
                id,
                role,
                LocalDateTime.now()
        );
    }

    protected long insertCategory(long id, String name, Long parentId, long createdBy) {
        if (!tableExists("category")) {
            return id;
        }

        jdbcTemplate.update(
                """
                INSERT INTO category (id, team_id, parent_id, name, created_by, created_at, updated_at)
                VALUES (?, 1, ?, ?, ?, ?, ?)
                """,
                id,
                parentId,
                name,
                createdBy,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return id;
    }

    protected long insertBookmark(long id, long categoryId, long createdBy, String title) {
        if (!tableExists("bookmark")) {
            return id;
        }

        jdbcTemplate.update(
                """
                INSERT INTO bookmark (id, team_id, category_id, title, url, description, created_by, created_at, updated_at)
                VALUES (?, 1, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                categoryId,
                title,
                "https://example.com/" + id,
                title + " description",
                createdBy,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return id;
    }

    protected String bearerTokenFor(String username) {
        return "Bearer " + jwtTokenService.generateToken(username);
    }

    protected boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?",
                Integer.class,
                tableName.toUpperCase()
        );
        return count != null && count > 0;
    }

    private void deleteIfTableExists(String tableName) {
        if (tableExists(tableName)) {
            jdbcTemplate.update("DELETE FROM " + tableName);
        }
    }
}
