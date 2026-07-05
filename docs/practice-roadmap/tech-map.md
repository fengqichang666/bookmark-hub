# 技术地图（面试导向）

> 一张表看清：每个后端技术点在项目里落在哪个功能上、面试能讲什么。

---

## 一、按技术点索引

| 技术点 | 落地功能 | 涉及文件（目标） | 面试可讲的点 |
|---|---|---|---|
| Spring Boot | 全项目 | `BookmarkHubApplication.java` | 自动装配、starter 原理 |
| MyBatis-Plus | 所有 CRUD | `**/mapper/*.java` | 通用 Mapper、条件构造器、逻辑删除、分页插件 |
| Flyway | 数据库版本管理 | `resources/db/migration/` | 迁移脚本、V vs R、生产回滚策略 |
| Spring Security | 登录 + 权限 | `security/`、`auth/` | 过滤器链、自定义 Filter |
| JWT | 无状态登录 | `auth/JwtTokenService.java` | 无状态 vs Session、刷新 token、注销问题 |
| RBAC | 团队多角色权限 | `security/rbac/`（阶段四） | 4 级模型、缓存策略、数据权限 |
| Redis 缓存 | 热点书签、用户 profile | `common/cache/`（阶段一） | 缓存三兄弟、Cache-Aside、双删 |
| Redisson 分布式锁 | 抓取去重 | `bookmark/mq/`（阶段二） | 可重入、看门狗、误释放 |
| RabbitMQ | 异步抓网页、削峰、死信 | `common/mq/`（阶段二） | 交换机类型、消息可靠性、幂等、死信 |
| @Scheduled | 简单定时 | `dashboard/schedule/`（阶段二） | 单机限制、集群下的问题 |
| XXL-JOB | 死链检测、回刷脚本 | `jobs/`（阶段二） | 调度中心 vs 执行器、分片、故障转移 |
| EasyExcel | Excel 导入导出 | `importing/`（阶段一） | 大文件流式读、监听器、多 Sheet |
| Elasticsearch | 全文搜索、聚合 | `search/`（阶段三） | 倒排索引、IK 分词、mapping、DSL、双写 |
| MinIO | 头像 / 封面 / favicon | `common/storage/`（阶段三） | 对象存储、预签名、秒传 |
| WebSocket | 团队协作实时推送 | `realtime/`（阶段四） | 握手鉴权、集群推送、心跳 |
| 邮件 | 邀请、告警 | `notification/`（阶段四） | 异步发送、模板、失败重试 |
| AOP | 操作日志、限流、权限 | `common/aspect/`（阶段一/四） | 切面、通知类型、执行顺序 |
| 全局异常 | 统一异常处理 | `common/exception/`（阶段一） | @RestControllerAdvice、错误码规范 |
| 事务 | 批量导入、团队解散 | 所有 Service | 传播行为、回滚策略、失效场景 |
| 限流 Bucket4j | 抓取接口防爬 | `common/ratelimit/`（阶段四） | 令牌桶 vs 漏桶、单机 vs 分布式 |
| Actuator + Prometheus | 应用指标 | `application.yml`（阶段五） | 自定义指标、告警 |
| SkyWalking | 链路追踪 | agent（阶段五） | traceId 传播、跨线程 |
| Docker Compose | 一键部署 | `docker-compose.yml`（阶段五） | 镜像分层、多阶段构建 |

---

## 二、按业务功能索引

反过来查：这个功能用到了哪些技术。

### 用户登录注册
Spring Security · JWT · BCrypt · Redis（token 黑名单）· 全局异常 · 邮件（激活/找回）

### 书签 CRUD
MyBatis-Plus · 事务 · 逻辑删除 · Redis 缓存 · AOP 日志 · WebSocket 推送（新建时）· ES 同步

### 书签导入
EasyExcel · 事务 · MQ 削峰（大批量拆消息）· 分布式锁（防重复导入）· 异常处理

### 书签导出
EasyExcel · 流式写 · MinIO（大文件存对象存储 + 预签名下载）· AOP 记录

### 书签抓取网页信息
MQ 异步 · Jsoup · Redisson 锁 · MinIO（favicon）· 重试 + 死信 · WebSocket 通知

### 全文搜索
Elasticsearch · IK · 高亮 · 聚合 · MySQL 双写（AFTER_COMMIT）· 后续可换 canal

### 点击排行
Redis ZSet · 定时任务（每小时落库）· 分布式锁

### 团队协作
RBAC · WebSocket · 站内信 · 邮件 · 数据权限（MyBatis 拦截器）

### 死链检测
XXL-JOB 分片 · HTTP 检测 · MQ · 邮件告警

### 报表
定时任务 · 复杂 SQL · Redis 缓存 · Grafana

### 数据订正 / 回刷
XXL-JOB 手动触发 · 分批 + sleep · 事务 · 幂等

---

## 三、按面试题索引

| 常见面试题 | 项目里的答法 |
|---|---|
| "讲下你项目的架构" | 前后端分离，Spring Boot 单体，Redis + MySQL + RabbitMQ + ES + MinIO，XXL-JOB 调度，Prometheus + SkyWalking 可观测 |
| "MQ 为什么用它" | 三件事：解耦（抓网页）、削峰（批量导入）、异步（邮件通知） |
| "缓存一致性怎么做" | 更新数据库后延迟双删；读走 Cache-Aside；热点提前预热 |
| "分布式锁怎么用的" | Redisson 加书签 URL 锁，防止同一个 URL 多用户同时抓取；看门狗自动续期 |
| "怎么保证消息不丢" | Confirm 机制 + 持久化 + 手动 ACK + 死信队列 + 定时补偿任务 |
| "怎么保证幂等" | 消息带唯一 ID，消费前查 Redis / DB；关键操作走去重表 |
| "怎么定位慢接口" | SkyWalking 看链路，找到慢在哪一步；慢查询看 MyBatis 日志 + explain |
| "线上出问题怎么排查" | traceId 关联所有日志，Grafana 看指标突刺，SkyWalking 看那次请求 |
| "怎么做权限" | RBAC 四级：角色-权限-菜单-按钮；JWT 里只放 userId，权限实时查 + 缓存；数据权限用 MyBatis 拦截器 |
| "定时任务和调度平台区别" | @Scheduled 单机、无重试、不能手动触发；XXL-JOB 分布式、可分片、有日志、可手动跑 |
| "线上怎么回刷数据" | XXL-JOB 挂 Handler 手动触发；分批 + limit + sleep；预先备份表；执行前 DBA 审 SQL |
| "为什么用 ES 不用 MySQL LIKE" | LIKE 不走索引、不分词、慢；ES 倒排索引 + 分词 + 高亮 + 聚合 |

---

## 四、能写进简历的亮点（3-5 个精选）

按重要性排序，简历上挑最能讲的 3-5 个：

1. **基于 RabbitMQ + Redisson 的书签内容异步抓取**：解耦、削峰、幂等、失败重试，QPS 从 X 提升到 Y。
2. **XXL-JOB 分片调度的死链检测**：全表 X 万条 URL，分片跑，单次 Y 分钟完成；同一套框架挂回刷数据脚本。
3. **Elasticsearch 全文搜索**：IK 中文分词，MySQL 双写 → 后升级到 canal 监听 binlog，搜索延迟 <100ms。
4. **RBAC 四级权限 + 数据权限**：菜单/按钮/接口三级 + MyBatis 拦截器实现"只看本团队数据"。
5. **WebSocket 集群推送**：Redis Pub/Sub 广播解决多实例下的粘性问题，支持万人在线。
6. **可观测**：Prometheus + Grafana + SkyWalking + Loki 全套，traceId 贯穿业务日志和链路。

---

## 五、每个阶段完成后自测

跑完一个阶段，能不能回答这些问题（不能就补功课）：

**阶段一**
- 缓存三兄弟（穿透 / 击穿 / 雪崩）分别怎么处理？
- MyBatis-Plus 的乐观锁怎么用？
- AOP 五种通知的执行顺序？
- Excel 大文件为什么不能一次读完？

**阶段二**
- RabbitMQ 四种交换机的区别？
- 消息可靠投递的三个环节？
- Redisson 的看门狗是什么？
- XXL-JOB 的分片是怎么分的？

**阶段三**
- 倒排索引原理？
- ES mapping 里 keyword 和 text 区别？
- MySQL 到 ES 的同步方案有几种？各自优缺点？
- 预签名 URL 是什么原理？

**阶段四**
- 数据权限怎么做到 SQL 层面自动过滤？
- JWT 无状态和分布式 Session 各自的优缺点？
- WebSocket 集群下怎么找到目标用户？
- 令牌桶和漏桶的区别？

**阶段五**
- traceId 怎么在跨线程 / 异步任务 / MQ 消费里传下去？
- Prometheus 拉模式 vs 推模式？
- 优雅停机的关键步骤？

---

**每次实现完一个功能，回来更新这张表**。这就是你的项目"技术索引"。
