package com.bookmarkhub.config;

import com.bookmarkhub.shared.BizException;
import com.bookmarkhub.shared.ErrorCode;
import com.bookmarkhub.shared.Result;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 全局异常处理：把各类异常统一转成 {@link Result}，保证成功/失败响应结构一致。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode, exception.getMessage()));
    }

    /** @Valid 校验失败：收集字段错误，避免前端只拿到一句笼统的 400。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(this::describeFieldError)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(Result.error(ErrorCode.BAD_REQUEST, detail.isEmpty()
                        ? ErrorCode.BAD_REQUEST.getMessage()
                        : detail));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDenied(AccessDeniedException exception) {
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(Result.error(ErrorCode.FORBIDDEN, exception.getMessage()));
    }

    /** 兼容仍在使用 ResponseStatusException 的历史代码。 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Result<Void>> handleResponseStatusException(ResponseStatusException exception) {
        ErrorCode errorCode = switch (exception.getStatus()) {
            case NOT_FOUND -> ErrorCode.NOT_FOUND;
            case UNAUTHORIZED -> ErrorCode.UNAUTHORIZED;
            case FORBIDDEN -> ErrorCode.FORBIDDEN;
            case CONFLICT -> ErrorCode.CONFLICT;
            case BAD_REQUEST -> ErrorCode.BAD_REQUEST;
            default -> ErrorCode.INTERNAL_ERROR;
        };
        return ResponseEntity.status(exception.getStatus())
                .body(Result.error(errorCode, exception.getReason() == null
                        ? errorCode.getMessage()
                        : exception.getReason()));
    }

    /** 兜底：不把堆栈或内部信息暴露给调用方，只在服务端日志里留全量细节。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpectedException(Exception exception) {
        log.error("Unhandled exception", exception);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getHttpStatus())
                .body(Result.error(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage()));
    }

    private String describeFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
