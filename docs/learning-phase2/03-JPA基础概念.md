# Day 3-5: Spring Data JPA核心

> 前端类比：JPA ≈ Prisma/TypeORM，自动生成SQL，面向对象操作数据库

## 1. JPA是什么？

### 概念理解

**JPA (Java Persistence API)** = Java持久化API
- **ORM框架**：Object-Relational Mapping（对象关系映射）
- **目标**：用Java对象操作数据库，不写SQL
- **实现**：Hibernate是最流行的JPA实现

### 前端对比

```javascript
// Prisma/TypeORM - 前端ORM
const user = await prisma.user.create({
  data: {
    name: 'Alice',
    email: 'alice@example.com'
  }
});

const users = await prisma.user.findMany({
  where: { email: { contains: '@example.com' } }
});
```

```java
// JPA - Java ORM
User user = new User();
user.setName("Alice");
user.setEmail("alice@example.com");
userRepository.save(user);  // 自动生成INSERT语句

List<User> users = userRepository.findByEmailContaining("@example.com");
// 自动生成SELECT语句
```

### 💡 关键点
- **不用写SQL**：JPA自动生成
- **面向对象**：操作Java对象，不是表
- **类型安全**：编译时检查，减少错误

---

## 2. 核心注解

### 2.1 @Entity - 实体类

```java
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity  // 标记为JPA实体
@Table(name = "bookmarks")  // 指定表名（可选，默认类名小写）
public class Bookmark {
    
    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增
    private Long id;
    
    @Column(name = "title", nullable = false, length = 255)  // 列配置
    private String title;
    
    @Column(nullable = false, length = 500)
    private String url;
    
    @Column(length = 100)
    private String category;
    
    @Column(columnDefinition = "TEXT")  // 长文本
    private String description;
    
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;
    
    @Column(name = "visit_count")
    private Integer visitCount = 0;
    
    @Column(name = "created_at", updatable = false)  // 不可更新
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 自动设置创建时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    // 自动更新修改时间
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数、Getter、Setter
    public Bookmark() {}
    
    public Bookmark(String title, String url, String category) {
        this.title = title;
        this.url = url;
        this.category = category;
    }
    
    // Getters and Setters...
}
```

### 注解对比表

| JPA注解 | 说明 | SQL对应 |
|---------|------|---------|
| `@Entity` | 标记实体类 | CREATE TABLE |
| `@Table(name="...")` | 指定表名 | TABLE名称 |
| `@Id` | 主键 | PRIMARY KEY |
| `@GeneratedValue` | 自增策略 | AUTO_INCREMENT |
| `@Column` | 列配置 | 列定义 |
| `@Transient` | 不持久化 | 不映射到表 |
| `@Temporal` | 日期类型 | DATE/TIMESTAMP |
| `@Enumerated` | 枚举类型 | VARCHAR/INT |

### 2.2 使用Lombok简化代码

```java
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Data  // 自动生成getter/setter/toString/equals/hashCode
@NoArgsConstructor  // 无参构造函数
@AllArgsConstructor  // 全参构造函数
public class Bookmark {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String url;
    
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Boolean isFavorite = false;
    
    private Integer visitCount = 0;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**pom.xml添加Lombok：**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

---

## 3. Repository接口

### 3.1 基础Repository

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 继承JpaRepository后，自动拥有以下方法：
    // save(entity)           - 保存/更新
    // findById(id)           - 根据ID查找
    // findAll()              - 查找所有
    // deleteById(id)         - 根据ID删除
    // count()                - 统计数量
    // existsById(id)         - 是否存在
}
```

### 前端对比
```typescript
// Prisma Client
const bookmark = await prisma.bookmark.create({ data: {...} });
const all = await prisma.bookmark.findMany();
const one = await prisma.bookmark.findUnique({ where: { id: 1 } });
await prisma.bookmark.delete({ where: { id: 1 } });
```

```java
// JPA Repository
Bookmark bookmark = bookmarkRepository.save(new Bookmark(...));
List<Bookmark> all = bookmarkRepository.findAll();
Optional<Bookmark> one = bookmarkRepository.findById(1L);
bookmarkRepository.deleteById(1L);
```

### 3.2 方法命名规则（自动生成查询）

```java
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    // 根据标题查找
    List<Bookmark> findByTitle(String title);
    // 生成SQL: SELECT * FROM bookmarks WHERE title = ?
    
    // 根据分类查找
    List<Bookmark> findByCategory(String category);
    // 生成SQL: SELECT * FROM bookmarks WHERE category = ?
    
    // 标题包含关键词
    List<Bookmark> findByTitleContaining(String keyword);
    // 生成SQL: SELECT * FROM bookmarks WHERE title LIKE %keyword%
    
    // 多条件查询
    List<Bookmark> findByCategoryAndIsFavorite(String category, Boolean isFavorite);
    // 生成SQL: SELECT * FROM bookmarks WHERE category = ? AND is_favorite = ?
    
    // 或条件
    List<Bookmark> findByTitleContainingOrUrlContaining(String title, String url);
    // 生成SQL: SELECT * FROM bookmarks WHERE title LIKE ? OR url LIKE ?
    
    // 排序
    List<Bookmark> findByCategoryOrderByCreatedAtDesc(String category);
    // 生成SQL: SELECT * FROM bookmarks WHERE category = ? ORDER BY created_at DESC
    
    // 统计
    Long countByCategory(String category);
    // 生成SQL: SELECT COUNT(*) FROM bookmarks WHERE category = ?
    
    // 是否存在
    Boolean existsByUrl(String url);
    // 生成SQL: SELECT COUNT(*) > 0 FROM bookmarks WHERE url = ?
    
    // 删除
    void deleteByCategory(String category);
    // 生成SQL: DELETE FROM bookmarks WHERE category = ?
    
    // Top N
    List<Bookmark> findTop10ByOrderByVisitCountDesc();
    // 生成SQL: SELECT * FROM bookmarks ORDER BY visit_count DESC LIMIT 10
}
```

### 方法命名关键词

| 关键词 | 说明 | 示例 |
|-------|------|------|
| `findBy` | 查询 | `findByTitle` |
| `countBy` | 统计 | `countByCategory` |
| `deleteBy` | 删除 | `deleteByCategory` |
| `existsBy` | 是否存在 | `existsByUrl` |
| `And` | 与条件 | `findByTitleAndCategory` |
| `Or` | 或条件 | `findByTitleOrUrl` |
| `Containing` | 包含 | `findByTitleContaining` |
| `StartingWith` | 开头 | `findByTitleStartingWith` |
| `EndingWith` | 结尾 | `findByTitleEndingWith` |
| `IgnoreCase` | 忽略大小写 | `findByTitleIgnoreCase` |
| `OrderBy` | 排序 | `findByOrderByCreatedAtDesc` |
| `Top/First` | 限制数量 | `findTop10By` |

---

## 4. 在Service中使用Repository

```java
package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional  // 事务管理
public class BookmarkService {
    
    @Autowired  // 自动注入
    private BookmarkRepository bookmarkRepository;
    
    // 或者用构造函数注入（推荐）
    // public BookmarkService(BookmarkRepository bookmarkRepository) {
    //     this.bookmarkRepository = bookmarkRepository;
    // }
    
    /**
     * 添加书签
     */
    public Bookmark addBookmark(String title, String url, String category) {
        // 检查URL是否已存在
        if (bookmarkRepository.existsByUrl(url)) {
            throw new IllegalArgumentException("该URL已存在");
        }
        
        Bookmark bookmark = new Bookmark();
        bookmark.setTitle(title);
        bookmark.setUrl(url);
        bookmark.setCategory(category);
        
        return bookmarkRepository.save(bookmark);
    }
    
    /**
     * 获取所有书签
     */
    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }
    
    /**
     * 根据ID获取书签
     */
    public Bookmark getBookmarkById(Long id) {
        return bookmarkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("书签不存在: " + id));
    }
    
    /**
     * 根据分类获取书签
     */
    public List<Bookmark> getBookmarksByCategory(String category) {
        return bookmarkRepository.findByCategory(category);
    }
    
    /**
     * 搜索书签
     */
    public List<Bookmark> searchBookmarks(String keyword) {
        return bookmarkRepository.findByTitleContainingOrUrlContaining(keyword, keyword);
    }
    
    /**
     * 更新书签
     */
    public Bookmark updateBookmark(Long id, String title, String url, String category) {
        Bookmark bookmark = getBookmarkById(id);
        
        if (title != null) bookmark.setTitle(title);
        if (url != null) bookmark.setUrl(url);
        if (category != null) bookmark.setCategory(category);
        
        return bookmarkRepository.save(bookmark);  // save方法同时用于新增和更新
    }
    
    /**
     * 删除书签
     */
    public void deleteBookmark(Long id) {
        if (!bookmarkRepository.existsById(id)) {
            throw new RuntimeException("书签不存在: " + id);
        }
        bookmarkRepository.deleteById(id);
    }
    
    /**
     * 增加访问次数
     */
    public void incrementVisitCount(Long id) {
        Bookmark bookmark = getBookmarkById(id);
        bookmark.setVisitCount(bookmark.getVisitCount() + 1);
        bookmarkRepository.save(bookmark);
    }
    
    /**
     * 切换收藏状态
     */
    public Bookmark toggleFavorite(Long id) {
        Bookmark bookmark = getBookmarkById(id);
        bookmark.setIsFavorite(!bookmark.getIsFavorite());
        return bookmarkRepository.save(bookmark);
    }
    
    /**
     * 获取热门书签（访问次数最多）
     */
    public List<Bookmark> getTopBookmarks(int limit) {
        return bookmarkRepository.findTop10ByOrderByVisitCountDesc();
    }
    
    /**
     * 统计分类数量
     */
    public Long countByCategory(String category) {
        return bookmarkRepository.countByCategory(category);
    }
}
```

---

## 5. 在Controller中使用Service

```java
package com.example.bookmark.controller;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    
    @Autowired
    private BookmarkService bookmarkService;
    
    /**
     * 获取所有书签
     * GET /api/bookmarks
     */
    @GetMapping
    public List<Bookmark> getAllBookmarks() {
        return bookmarkService.getAllBookmarks();
    }
    
    /**
     * 根据ID获取书签
     * GET /api/bookmarks/1
     */
    @GetMapping("/{id}")
    public Bookmark getBookmarkById(@PathVariable Long id) {
        return bookmarkService.getBookmarkById(id);
    }
    
    /**
     * 添加书签
     * POST /api/bookmarks
     * Body: {"title": "Google", "url": "https://google.com", "category": "搜索"}
     */
    @PostMapping
    public Bookmark addBookmark(@RequestBody BookmarkRequest request) {
        return bookmarkService.addBookmark(
            request.getTitle(),
            request.getUrl(),
            request.getCategory()
        );
    }
    
    /**
     * 更新书签
     * PUT /api/bookmarks/1
     */
    @PutMapping("/{id}")
    public Bookmark updateBookmark(
        @PathVariable Long id,
        @RequestBody BookmarkRequest request
    ) {
        return bookmarkService.updateBookmark(
            id,
            request.getTitle(),
            request.getUrl(),
            request.getCategory()
        );
    }
    
    /**
     * 删除书签
     * DELETE /api/bookmarks/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 搜索书签
     * GET /api/bookmarks/search?keyword=git
     */
    @GetMapping("/search")
    public List<Bookmark> searchBookmarks(@RequestParam String keyword) {
        return bookmarkService.searchBookmarks(keyword);
    }
    
    /**
     * 根据分类获取
     * GET /api/bookmarks/category/开发工具
     */
    @GetMapping("/category/{category}")
    public List<Bookmark> getByCategory(@PathVariable String category) {
        return bookmarkService.getBookmarksByCategory(category);
    }
    
    /**
     * 切换收藏
     * POST /api/bookmarks/1/favorite
     */
    @PostMapping("/{id}/favorite")
    public Bookmark toggleFavorite(@PathVariable Long id) {
        return bookmarkService.toggleFavorite(id);
    }
}

// 请求DTO
@Data
class BookmarkRequest {
    private String title;
    private String url;
    private String category;
}
```

---

## 6. 配置文件

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:bookmarkdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: update  # 自动更新表结构
      # create: 每次启动删除并创建表
      # create-drop: 启动创建，关闭删除
      # update: 更新表结构（推荐开发环境）
      # validate: 只验证，不修改
      # none: 不做任何操作（推荐生产环境）
    
    show-sql: true  # 控制台显示SQL
    properties:
      hibernate:
        format_sql: true  # 格式化SQL
        use_sql_comments: true  # 显示SQL注释
  
  h2:
    console:
      enabled: true
      path: /h2-console

logging:
  level:
    org.hibernate.SQL: DEBUG  # SQL日志
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE  # 参数日志
```

---

## 7. 测试

```java
package com.example.bookmark;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.service.BookmarkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookmarkServiceTest {
    
    @Autowired
    private BookmarkService bookmarkService;
    
    @Autowired
    private BookmarkRepository bookmarkRepository;
    
    @Test
    public void testAddBookmark() {
        Bookmark bookmark = bookmarkService.addBookmark(
            "Google", 
            "https://google.com", 
            "搜索引擎"
        );
        
        assertNotNull(bookmark.getId());
        assertEquals("Google", bookmark.getTitle());
    }
    
    @Test
    public void testFindByCategory() {
        // 添加测试数据
        bookmarkService.addBookmark("Google", "https://google.com", "搜索");
        bookmarkService.addBookmark("GitHub", "https://github.com", "开发");
        bookmarkService.addBookmark("Bing", "https://bing.com", "搜索");
        
        // 查询
        List<Bookmark> searchEngines = bookmarkService.getBookmarksByCategory("搜索");
        
        assertEquals(2, searchEngines.size());
    }
    
    @Test
    public void testSearch() {
        bookmarkService.addBookmark("GitHub", "https://github.com", "开发");
        bookmarkService.addBookmark("GitLab", "https://gitlab.com", "开发");
        
        List<Bookmark> results = bookmarkService.searchBookmarks("Git");
        
        assertEquals(2, results.size());
    }
}
```

---

## 8. 常见问题

### Q1: save()方法如何区分新增和更新？
**A:** 根据ID是否为null：
```java
Bookmark bookmark = new Bookmark();
bookmark.setTitle("Google");
bookmarkRepository.save(bookmark);  // 新增（ID为null）

bookmark.setTitle("Google搜索");
bookmarkRepository.save(bookmark);  // 更新（ID不为null）
```

### Q2: findById()返回Optional是什么？
**A:** Optional是Java 8的容器，避免空指针：
```java
// 方式1：orElseThrow
Bookmark bookmark = bookmarkRepository.findById(1L)
    .orElseThrow(() -> new RuntimeException("不存在"));

// 方式2：orElse
Bookmark bookmark = bookmarkRepository.findById(1L)
    .orElse(null);

// 方式3：isPresent
Optional<Bookmark> optional = bookmarkRepository.findById(1L);
if (optional.isPresent()) {
    Bookmark bookmark = optional.get();
}
```

### Q3: @Transactional有什么用？
**A:** 事务管理，保证数据一致性：
```java
@Transactional
public void transferBookmarks(Long fromUserId, Long toUserId) {
    // 如果中间出错，所有操作都会回滚
    List<Bookmark> bookmarks = findByUserId(fromUserId);
    bookmarks.forEach(b -> b.setUserId(toUserId));
    bookmarkRepository.saveAll(bookmarks);
}
```

---

## 📝 今日总结

你已经学会了：
- ✅ 理解JPA和ORM的概念
- ✅ 使用@Entity创建实体类
- ✅ 使用Repository接口操作数据库
- ✅ 掌握方法命名规则自动生成查询
- ✅ 在Service和Controller中使用JPA
- ✅ 配置JPA和数据源

**下一步：** [06-实体关系映射.md](./06-实体关系映射.md) - 学习一对多、多对多关系
