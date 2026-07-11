package com.bookmarkhub.dashboard.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewVO {

    private long bookmarkCount;
    private long categoryCount;
    private long memberCount;
}
