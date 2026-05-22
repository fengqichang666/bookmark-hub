package com.bookmarkhub.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookmarkhub.ApiIntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DashboardControllerTest extends ApiIntegrationTestSupport {

    @BeforeEach
    void setUpData() {
        insertMember(2L, "member1", "Member One", "MEMBER");
        insertCategory(1L, "Frontend", null, 1L);
        insertBookmark(1L, 1L, 1L, "React");
    }

    @Test
    void overviewReturnsCurrentCounts() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview")
                        .header("Authorization", bearerTokenFor("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarkCount").value(1))
                .andExpect(jsonPath("$.categoryCount").value(1))
                .andExpect(jsonPath("$.memberCount").value(2));
    }
}
