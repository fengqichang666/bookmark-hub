package com.bookmarkhub.bookmark;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookmarkhub.ApiIntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class BookmarkControllerTest extends ApiIntegrationTestSupport {

    @BeforeEach
    void setUpData() {
        insertMember(2L, "member1", "Member One", "MEMBER");
        insertMember(3L, "member2", "Member Two", "MEMBER");
        insertCategory(1L, "Frontend", null, 1L);
        insertBookmark(2L, 1L, 2L, "React");
    }

    @Test
    void memberCannotUpdateAnotherMembersBookmark() throws Exception {
        mockMvc.perform(put("/api/bookmarks/2")
                        .header("Authorization", bearerTokenFor("member2"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Renamed","url":"https://example.com","description":"x","categoryId":1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void memberCanCreateAndListBookmarks() throws Exception {
        mockMvc.perform(post("/api/bookmarks")
                        .header("Authorization", bearerTokenFor("member1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Spring","url":"https://spring.io","description":"docs","categoryId":1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring"));

        mockMvc.perform(get("/api/bookmarks")
                        .header("Authorization", bearerTokenFor("member1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].title").isArray());
    }
}
