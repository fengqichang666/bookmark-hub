# Day 6-10: Spring Boot快速开始

> 前端类比：Spring Boot ≈ Express/Nest.js，但更强大的企业级框架

## 1. Spring Boot是什么？

### 概念理解

**Spring Boot** = Spring框架的快速开发脚手架
- **约定优于配置**：开箱即用，无需大量配置
- **内嵌服务器**：无需部署到Tomcat，直接运行
- **自动配置**：根据依赖自动配置Bean
- **生产就绪**：内置监控、健康检查等功能

### 前端对比

```javascript
// Express - 前端Node.js框架
const express = require('express');
const app = express();

app.get('/api/hello', (req, res) => {
  res.json({ message: 'Hello World' });
});

app.listen(3000, () => {
  console.log('Server running on port 3000');
});
```

```java
// Spring Boot - Java后端框架
@SpringBootApplication
public class BookmarkApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookmarkApplication.class, args);
    }
}

@RestController
@RequestMapping("/api")
class HelloController {
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello World");
    }
}
```

### 💡 关键点
- **@SpringBootApplication**：启动类注解，包含自动配置
- **@RestController**：RESTful API控制器
- **内嵌Tomcat**：无需外部服务器，直接运行main方法

---

## 2. 创建第一个Spring Boot项目

### 方式1：Spring Initializr（推荐）

访问 https://start.spring.io/

**配置选项：**
- **Project**: Maven
- **Language**: Java
- **Spring Boot**: 3.2.0（选最新稳定版）
- **Group**: com.example
- **Artifact**: bookmark-hub
- **Packaging**: Jar
- **Java**: 17

**依赖选择：**
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok

点击"Generate"下载项目，解压后用IDEA打开。

### 方式2：IDEA创建（如果有Ultimate版）

1. File → New → Project
2. 选择 Spring Initializr
3. 配置同上
4. 选择依赖
5. 创建项目

### 方式3：Maven命令行

```bash
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=bookmark-hub \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```

---

## 3. 项目结构详解

```
bookmark-hub/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/bookmark/
│   │   │       ├── BookmarkApplication.java      # 启动类
│   │   │       ├── controller/                   # 控制器层
│   │   │       │   └── BookmarkController.java
│   │   │       ├── service/                      # 业务逻辑层
│   │   │       │   └── BookmarkService.java
│   │   │       ├── repository/                   # 数据访问层
│   │   │       │   └── BookmarkRepository.java
│   │   │       ├── model/                        # 实体类
│   │   │       │   └── Bookmark.java
│   │   │       └── dto/                          # 数据传输对象
│   │   │           └── BookmarkDTO.java
│   │   └── resources/
│   │       ├── application.yml                   # 配置文件
│   │       ├── static/                           # 静态资源
│   │       └── templates/                        # 模板文件
│   └── test/
│       └── java/
│           └── com/example/bookmark/
│               └── BookmarkApplicationTests.java
├── pom.xml
└── README.md
```

### 分层架构

```
前端请求
   ↓
Controller层（接收请求，返回响应）
   ↓
Service层（业务逻辑）
   ↓
Repository层（数据访问）
   ↓
数据库
```

### 前端对比

```
前端MVC/MVVM              Spring Boot分层
├── routes/           →  ├── controller/
├── services/         →  ├── service/
├── models/           →  ├── repository/ + model/
└── views/            →  └── (前后端分离，无此层)
```

---

## 4. 启动类详解

```java
package com.example.bookmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // 核心注解，包含3个注解：
// @Configuration - 配置类
// @EnableAutoConfiguration - 自动配置
// @ComponentScan - 组件扫描
public class BookmarkApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookmarkApplication.class, args);
    }
}
```

### 启动流程

1. **main方法启动**
2. **扫描@Component注解**（@Controller、@Service、@Repository）
3. **创建Spring容器**（ApplicationContext）
4. **自动配置**（根据pom.xml依赖）
5. **启动内嵌Tomcat**
6. **应用就绪**

### 运行项目

```bash
# 方式1：Maven命令
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/bookmark-hub-1.0.0.jar

# 方式3：IDEA运行
# 右键 BookmarkApplication.java → Run
```

访问：http://localhost:8080

---

## 5. 第一个RESTful API

### 创建Controller

```java
package com.example.bookmark.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController  // @Controller + @ResponseBody
@RequestMapping("/api")  // 基础路径
public class HelloController {
    
    // GET /api/hello
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello Spring Boot!");
    }
    
    // GET /api/hello/Alice
    @GetMapping("/hello/{name}")
    public Map<String, String> helloName(@PathVariable String name) {
        return Map.of("message", "Hello " + name + "!");
    }
    
    // GET /api/greet?name=Alice
    @GetMapping("/greet")
    public Map<String, String> greet(@RequestParam String name) {
        return Map.of("message", "Hello " + name + "!");
    }
    
    // POST /api/echo
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        return Map.of(
            "received", data,
            "timestamp", System.currentTimeMillis()
        );
    }
}
```

### 注解说明

| 注解 | 说明 | 前端类比 |
|------|------|---------|
| `@RestController` | RESTful控制器 | `app.use()` |
| `@RequestMapping` | 路由基础路径 | `router.use('/api')` |
| `@GetMapping` | GET请求 | `app.get()` |
| `@PostMapping` | POST请求 | `app.post()` |
| `@PutMapping` | PUT请求 | `app.put()` |
| `@DeleteMapping` | DELETE请求 | `app.delete()` |
| `@PathVariable` | 路径参数 | `req.params` |
| `@RequestParam` | 查询参数 | `req.query` |
| `@RequestBody` | 请求体 | `req.body` |

### 测试API

```bash
# GET请求
curl http://localhost:8080/api/hello

# 路径参数
curl http://localhost:8080/api/hello/Alice

# 查询参数
curl "http://localhost:8080/api/greet?name=Alice"

# POST请求
curl -X POST http://localhost:8080/api/echo \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","age":25}'
```

---

## 6. 完整的书签API

### 6.1 实体类

```java
package com.example.bookmark.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

### 6.2 Repository

```java
package com.example.bookmark.repository;

import com.example.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByCategory(String category);
    List<Bookmark> findByTitleContainingOrUrlContaining(String title, String url);
    List<Bookmark> findTop10ByOrderByVisitCountDesc();
    Boolean existsByUrl(String url);
}
```

### 6.3 Service

```java
package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.repository.BookmarkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // Lombok生成构造函数注入
public class BookmarkService {
    
    private final BookmarkRepository bookmarkRepository;
    
    public Bookmark createBookmark(Bookmark bookmark) {
        if (bookmarkRepository.existsByUrl(bookmark.getUrl())) {
            throw new IllegalArgumentException("URL已存在");
        }
        return bookmarkRepository.save(bookmark);
    }
    
    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }
    
    public Bookmark getBookmarkById(Long id) {
        return bookmarkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("书签不存在: " + id));
    }
    
    public List<Bookmark> searchBookmarks(String keyword) {
        return bookmarkRepository.findByTitleContainingOrUrlContaining(keyword, keyword);
    }
    
    public Bookmark updateBookmark(Long id, Bookmark updated) {
        Bookmark bookmark = getBookmarkById(id);
        bookmark.setTitle(updated.getTitle());
        bookmark.setUrl(updated.getUrl());
        bookmark.setCategory(updated.getCategory());
        bookmark.setDescription(updated.getDescription());
        return bookmarkRepository.save(bookmark);
    }
    
    public void deleteBookmark(Long id) {
        bookmarkRepository.deleteById(id);
    }
}
```

### 6.4 Controller

```java
package com.example.bookmark.controller;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    /**
     * 获取所有书签
     * GET /api/bookmarks
     */
    @GetMapping
    public ResponseEntity<List<Bookmark>> getAllBookmarks() {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks());
    }
    
    /**
     * 根据ID获取书签
     * GET /api/bookmarks/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bookmark> getBookmarkById(@PathVariable Long id) {
        return ResponseEntity.ok(bookmarkService.getBookmarkById(id));
    }
    
    /**
     * 创建书签
     * POST /api/bookmarks
     */
    @PostMapping
    public ResponseEntity<Bookmark> createBookmark(@RequestBody Bookmark bookmark) {
        Bookmark created = bookmarkService.createBookmark(bookmark);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * 更新书签
     * PUT /api/bookmarks/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<Bookmark> updateBookmark(
        @PathVariable Long id,
        @RequestBody Bookmark bookmark
    ) {
        return ResponseEntity.ok(bookmarkService.updateBookmark(id, bookmark));
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
    public ResponseEntity<List<Bookmark>> searchBookmarks(@RequestParam String keyword) {
        return ResponseEntity.ok(bookmarkService.searchBookmarks(keyword));
    }
}
```

---

## 7. 配置文件

### application.yml

```yaml
# 服务器配置
server:
  port: 8080  # 端口号
  servlet:
    context-path: /  # 应用路径

# 数据源配置
spring:
  datasource:
    url: jdbc:h2:mem:bookmarkdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  # H2控制台
  h2:
    console:
      enabled: true
      path: /h2-console

# 日志配置
logging:
  level:
    root: INFO
    com.example.bookmark: DEBUG
    org.hibernate.SQL: DEBUG
```

### 多环境配置

```yaml
# application.yml（默认配置）
spring:
  profiles:
    active: dev  # 激活dev环境

---
# application-dev.yml（开发环境）
spring:
  datasource:
    url: jdbc:h2:mem:bookmarkdb
  jpa:
    show-sql: true

---
# application-prod.yml（生产环境）
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookmark
    username: root
    password: ${DB_PASSWORD}  # 从环境变量读取
  jpa:
    show-sql: false
```

---

## 8. 测试完整API

### 使用Postman测试

**1. 创建书签**
```
POST http://localhost:8080/api/bookmarks
Content-Type: application/json

{
  "title": "Google",
  "url": "https://google.com",
  "category": "搜索引擎",
  "description": "全球最大的搜索引擎"
}
```

**2. 获取所有书签**
```
GET http://localhost:8080/api/bookmarks
```

**3. 搜索书签**
```
GET http://localhost:8080/api/bookmarks/search?keyword=google
```

**4. 更新书签**
```
PUT http://localhost:8080/api/bookmarks/1
Content-Type: application/json

{
  "title": "Google搜索",
  "url": "https://google.com",
  "category": "搜索"
}
```

**5. 删除书签**
```
DELETE http://localhost:8080/api/bookmarks/1
```

---

## 9. 常见问题

### Q1: 启动报错"端口被占用"？
**A:** 修改端口号：
```yaml
server:
  port: 8081  # 改为其他端口
```

### Q2: @Autowired报红线？
**A:** 推荐用构造函数注入：
```java
// ❌ 不推荐
@Autowired
private BookmarkService bookmarkService;

// ✅ 推荐
private final BookmarkService bookmarkService;

public BookmarkController(BookmarkService bookmarkService) {
    this.bookmarkService = bookmarkService;
}

// ✅ 或用Lombok
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;
}
```

### Q3: 返回JSON中文乱码？
**A:** 添加配置：
```yaml
spring:
  http:
    encoding:
      charset: UTF-8
      force: true
```

---

## 📝 今日总结

你已经学会了：
- ✅ 创建Spring Boot项目
- ✅ 理解项目结构和分层架构
- ✅ 编写RESTful API
- ✅ 使用@RestController和路由注解
- ✅ 整合JPA实现完整CRUD
- ✅ 配置application.yml

**下一步：** [07-依赖注入与Bean.md](./07-依赖注入与Bean.md) - 深入理解Spring核心概念
