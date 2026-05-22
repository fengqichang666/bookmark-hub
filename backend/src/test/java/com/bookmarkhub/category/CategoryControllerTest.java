package com.bookmarkhub.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookmarkhub.ApiIntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CategoryControllerTest extends ApiIntegrationTestSupport {

    @BeforeEach
    void setUpMembers() {
        insertMember(2L, "member1", "Member One", "MEMBER");
    }

    @Test
    void memberCannotCreateCategory() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", bearerTokenFor("member1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Frontend","parentId":null}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateAndListCategories() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", bearerTokenFor("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Frontend","parentId":null}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Frontend"));

        mockMvc.perform(get("/api/categories")
                        .header("Authorization", bearerTokenFor("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Frontend"));
    }
}
