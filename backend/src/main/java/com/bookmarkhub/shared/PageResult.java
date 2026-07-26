package com.bookmarkhub.shared;

import java.util.List;

/**
 * 分页结果。用于替代直接返回 MyBatis-Plus 的 IPage：
 * IPage 是 ORM 类型，暴露到 HTTP 契约上会让前端耦合到持久层实现。
 */
public record PageResult<T>(List<T> items, long page, long size, long total) {

    public static <T> PageResult<T> of(List<T> items, long page, long size, long total) {
        return new PageResult<>(items, page, size, total);
    }
}
