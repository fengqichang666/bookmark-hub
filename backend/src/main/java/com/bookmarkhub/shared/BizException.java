package com.bookmarkhub.shared;

import lombok.Getter;

/**
 * 业务异常。service 层抛这个，由 GlobalExceptionHandler 统一转成 Result + HTTP 状态码，
 * 避免 service 直接依赖 web 层的 ResponseStatusException。
 */
@Getter
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /** 保留底层异常，方便在服务端日志里定位根因。 */
    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
