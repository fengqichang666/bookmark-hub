package com.bookmarkhub.dashboard.service;

import com.bookmarkhub.dashboard.vo.DashboardOverviewVO;

public interface DashboardService {

    DashboardOverviewVO overview(String username);
}
