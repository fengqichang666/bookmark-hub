package com.bookmarkhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * 请求日志配置：把所有 HTTP 请求打印到控制台。
 * 仅在 dev profile 生效，生产环境不启用，避免打印敏感数据。
 */
@Configuration
@Profile("dev")
public class WebLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);      // 打印查询参数 ?a=1&b=2
        filter.setIncludePayload(true);          // 打印请求体（POST/PUT 的 JSON）
        filter.setMaxPayloadLength(2000);        // 请求体最多打 2000 字符，避免刷屏
        filter.setIncludeHeaders(false);         // 不打请求头（含 Authorization token，避免泄漏）
        filter.setIncludeClientInfo(true);       // 打印客户端 IP
        filter.setBeforeMessagePrefix("REQ  → ");
        filter.setAfterMessagePrefix("REQ  ← ");
        return filter;
    }
}
