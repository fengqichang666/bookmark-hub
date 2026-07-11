package com.bookmarkhub.bookmark.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkSummaryVO {

    private Long id;
    private String title;
    private String url;
    private String creatorName;
}
