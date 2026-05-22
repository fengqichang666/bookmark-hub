package com.bookmarkhub.importing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookmarkhub.ApiIntegrationTestSupport;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class ImportControllerTest extends ApiIntegrationTestSupport {

    @BeforeEach
    void setUpData() {
        insertCategory(1L, "Imported", null, 1L);
    }

    @Test
    void parseHtmlReturnsPreviewRows() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "bookmarks.html",
                "text/html",
                """
                <!DOCTYPE NETSCAPE-Bookmark-file-1>
                <DL><p>
                  <DT><H3>开发</H3>
                  <DL><p>
                    <DT><A HREF="https://react.dev">React</A>
                  </DL><p>
                </DL><p>
                """.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/imports/parse")
                        .file(file)
                        .header("Authorization", bearerTokenFor("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value("React"))
                .andExpect(jsonPath("$.items[0].url").value("https://react.dev"))
                .andExpect(jsonPath("$.items[0].folderPath").value("开发"));
    }

    @Test
    void confirmImportPersistsBookmarksAndRecord() throws Exception {
        mockMvc.perform(post("/api/imports/confirm")
                        .header("Authorization", bearerTokenFor("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fileName": "bookmarks.html",
                                  "categoryId": 1,
                                  "items": [
                                    {"title":"React","url":"https://react.dev","folderPath":"开发"},
                                    {"title":"Spring","url":"https://spring.io","folderPath":"后端"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.failedCount").value(0));

        Integer bookmarkCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bookmark", Integer.class);
        Integer importRecordCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM import_record", Integer.class);

        org.junit.jupiter.api.Assertions.assertEquals(2, bookmarkCount);
        org.junit.jupiter.api.Assertions.assertEquals(1, importRecordCount);
    }
}
