-- 书签改为逻辑删除：保留行数据，便于操作日志溯源。
-- 0 = 未删除，1 = 已删除，与 MyBatis-Plus @TableLogic 的默认约定一致。
ALTER TABLE bookmark ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0;

-- 列表/详情查询都会带上 deleted = 0，配合 team_id 建复合索引
CREATE INDEX idx_bookmark_team_deleted ON bookmark (team_id, deleted);
