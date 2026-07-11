CREATE TABLE operation_log
(
    id             BIGINT PRIMARY KEY auto_increment,
    -- 操作人快照
    operator_id    BIGINT      NOT NULL,
    operator_name  VARCHAR(64) NOT NULL,
    operator_role  VARCHAR(16) NOT NULL,
    -- 团队归属
    team_id        BIGINT      NOT NULL,
    -- 书签快照
    bookmark_id    BIGINT,
    bookmark_title VARCHAR(255),

    -- 分类快照
    category_id    BIGINT,
    category_name  VARCHAR(100),

    -- 操作类型
    action         VARCHAR(32) NOT NULL, -- CREATE / UPDATE / DELETE
    detail         TEXT,

    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_op_log_team_time ON operation_log (team_id, created_at);
CREATE INDEX idx_op_log_operator ON operation_log (operator_id);
CREATE INDEX idx_op_log_bookmark ON operation_log (bookmark_id);
CREATE INDEX idx_op_log_category ON operation_log (category_id);
CREATE INDEX idx_op_log_role ON operation_log (operator_role);