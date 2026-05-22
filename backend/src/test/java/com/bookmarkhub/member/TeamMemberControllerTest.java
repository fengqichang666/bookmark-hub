package com.bookmarkhub.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookmarkhub.ApiIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TeamMemberControllerTest extends ApiIntegrationTestSupport {

    @Test
    void adminCanCreateAndListMembers() throws Exception {
        mockMvc.perform(post("/api/members")
                        .header("Authorization", bearerTokenFor("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"member1","password":"Password@123","displayName":"Member One","role":"MEMBER"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("member1"));

        mockMvc.perform(get("/api/members")
                        .header("Authorization", bearerTokenFor("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].username").isArray());
    }
}
