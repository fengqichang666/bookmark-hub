package com.bookmarkhub.operationlog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.operationlog.dto.OperationLogQuery;
import com.bookmarkhub.operationlog.entity.OperationLog;
import com.bookmarkhub.operationlog.enums.OperationAction;
import com.bookmarkhub.operationlog.mapper.OperationLogMapper;
import com.bookmarkhub.operationlog.service.OperationLogService;
import com.bookmarkhub.operationlog.vo.OperationLogVO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
        implements OperationLogService {

    private final AuthService authService;

    @Override
    public void record(AuthActor actor,
                       OperationAction action,
                       Bookmark bookmark,
                       Category category,
                       String detail) {
        OperationLog log = new OperationLog();
        log.setOperatorId(actor.userId());
        log.setOperatorName(actor.displayName());
        log.setOperatorRole(actor.role());
        log.setTeamId(actor.teamId());
        log.setBookmarkId(bookmark == null ? null : bookmark.getId());
        log.setBookmarkTitle(bookmark == null ? null : bookmark.getTitle());
        log.setCategoryId(category == null ? null : category.getId());
        log.setCategoryName(category == null ? null : category.getName());
        log.setAction(action.name());
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        this.save(log);
    }

    @Override
    public IPage<OperationLogVO> list(String username, OperationLogQuery query) {
        AuthActor actor = authService.requireActor(username);
        Page<OperationLog> page = new Page<>(query.pageOrDefault(), query.sizeOrDefault());
        IPage<OperationLog> raw = this.lambdaQuery()
                .eq(OperationLog::getTeamId, actor.teamId())
                .eq(query.getRole() != null, OperationLog::getOperatorRole, query.getRole())
                .eq(query.getOperatorId() != null, OperationLog::getOperatorId, query.getOperatorId())
                .eq(query.getCategoryId() != null, OperationLog::getCategoryId, query.getCategoryId())
                .eq(query.getBookmarkId() != null, OperationLog::getBookmarkId, query.getBookmarkId())
                .orderByDesc(OperationLog::getCreatedAt)
                .page(page);

        List<OperationLogVO> items = raw.getRecords().stream()
                .map(this::toVO)
                .toList();
        Page<OperationLogVO> result = new Page<>(raw.getCurrent(), raw.getSize(), raw.getTotal());
        result.setRecords(items);
        return result;
    }

    private OperationLogVO toVO(OperationLog log) {
        return new OperationLogVO(
                log.getId(),
                log.getAction(),
                log.getOperatorName(),
                log.getOperatorRole(),
                log.getBookmarkTitle(),
                log.getCategoryName(),
                log.getCreatedAt()
        );
    }
}
