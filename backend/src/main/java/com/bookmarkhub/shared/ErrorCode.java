package com.bookmarkhub.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务错误码。
 *
 * <p>code 与 HTTP 状态码解耦：HTTP 状态给网关/浏览器看，code 给前端做分支判断。
 * 约定 0 表示成功，业务错误按模块分段（1xxx 通用、2xxx 认证、3xxx 书签…）。
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "OK", HttpStatus.OK),

    BAD_REQUEST(1000, "请求参数不合法", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1001, "资源不存在", HttpStatus.NOT_FOUND),
    CONFLICT(1002, "资源已存在", HttpStatus.CONFLICT),
    INTERNAL_ERROR(1003, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHORIZED(2000, "登录状态无效", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(2001, "用户名或密码错误", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(2002, "没有操作权限", HttpStatus.FORBIDDEN),

    CATEGORY_NOT_FOUND(3000, "分类不存在", HttpStatus.NOT_FOUND),
    BOOKMARK_NOT_FOUND(3001, "书签不存在", HttpStatus.NOT_FOUND),
    USERNAME_TAKEN(3002, "用户名已存在", HttpStatus.CONFLICT),
    IMPORT_FILE_UNREADABLE(3003, "导入文件无法解析", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
