# Day 7-8: 依赖注入与Bean管理

> 前端类比：依赖注入 ≈ React Context + Provider，但更强大

## 1. 什么是依赖注入（DI）？

### 概念理解

**依赖注入 (Dependency Injection)** = 控制反转 (IoC) 的一种实现方式
- **不自己创建依赖对象**，而是由Spring容器注入
- **解耦**：降低类之间的耦合度
- **易测试**：方便Mock依赖

### 前端对比

```javascript
// 前端：手动创建依赖（紧耦合）
class BookmarkController {
  constructor() {
    this.service = new BookmarkService();  // 手动创建
  }
}

// React Context：依赖注入的一种形式
const BookmarkContext = React.createContext();

function BookmarkController() {
  const service = useContext(BookmarkContext);  // 注入依赖
  // ...
}
```

```java
// Java：没有依赖注入（紧耦合）
public class BookmarkController {
    private BookmarkService service = new BookmarkService();  // 手动创建
}

// Spring：依赖注入（松耦合）
@RestController
public class BookmarkController {
    private final BookmarkService service;
    
    // Spring自动注入
    public BookmarkController(BookmarkService service) {
        this.service = service;
    }
}
```

### 💡 关键点
- **不用new**：对象由Spring容器创建和管理
- **自动注入**：通过构造函数、字段或Setter注入
- **单例模式**：默认Bean是单例，整个应用共享一个实例

---

## 2. Bean是什么？

### 概念

**Bean** = Spring容器管理的对象
- 由Spring创建、配置、管理
- 存储在Spring容器（ApplicationContext）中
- 可以被注入到其他Bean中

### Bean的生命周期

```
1. 实例化（Instantiation）
   ↓
2. 属性赋值（Populate Properties）
   ↓
3. 初始化（Initialization）
   - @PostConstruct
   - InitializingBean.afterPropertiesSet()
   ↓
4. 使用（In Use）
   ↓
5. 销毁（Destruction）
   - @PreDestroy
   - DisposableBean.destroy()
```

---

## 3. 创建Bean的方式

### 3.1 组件注解（最常用）

```java
// @Component - 通用组件
@Component
public class BookmarkHelper {
    public String formatUrl(String url) {
        return url.toLowerCase();
    }
}

// @Service - 业务逻辑层
@Service
public class BookmarkService {
    // ...
}

// @Repository - 数据访问层
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // ...
}

// @Controller - Web控制器
@Controller
public class BookmarkViewController {
    // 返回视图
}

// @RestController - RESTful API控制器
@RestController
public class BookmarkApiController {
    // 返回JSON
}
```

### 注解对比

| 注解 | 层级 | 说明 |
|------|------|------|
| `@Component` | 通用 | 通用组件，其他注解的父类 |
| `@Service` | 业务层 | 业务逻辑，语义化 |
| `@Repository` | 数据层 | 数据访问，自动异常转换 |
| `@Controller` | 控制层 | MVC控制器，返回视图 |
| `@RestController` | 控制层 | RESTful API，返回JSON |

### 3.2 @Configuration + @Bean

```java
@Configuration  // 配置类
public class AppConfig {
    
    // 创建Bean
    @Bean
    public BookmarkFormatter bookmarkFormatter() {
        return new BookmarkFormatter();
    }
    
    // Bean可以依赖其他Bean
    @Bean
    public BookmarkValidator bookmarkValidator(BookmarkFormatter formatter) {
        return new BookmarkValidator(formatter);
    }
    
    // 条件创建Bean
    @Bean
    @ConditionalOnProperty(name = "feature.cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
```

### 3.3 第三方Bean

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
```

---

## 4. 依赖注入的三种方式

### 4.1 构造函数注入（推荐）

```java
@Service
public class BookmarkService {
    
    private final BookmarkRepository repository;
    private final BookmarkValidator validator;
    
    // 构造函数注入（推荐）
    public BookmarkService(
        BookmarkRepository repository,
        BookmarkValidator validator
    ) {
        this.repository = repository;
        this.validator = validator;
    }
}

// 使用Lombok简化
@Service
@RequiredArgsConstructor  // 自动生成构造函数
public class BookmarkService {
    private final BookmarkRepository repository;
    private final BookmarkValidator validator;
}
```

**优点：**
- ✅ 依赖明确，不可变（final）
- ✅ 易于测试（可以手动传入Mock对象）
- ✅ 避免循环依赖

### 4.2 字段注入（不推荐）

```java
@Service
public class BookmarkService {
    
    @Autowired  // 字段注入
    private BookmarkRepository repository;
    
    @Autowired
    private BookmarkValidator validator;
}
```

**缺点：**
- ❌ 依赖不明确
- ❌ 难以测试（需要Spring容器）
- ❌ 不能用final

### 4.3 Setter注入（少用）

```java
@Service
public class BookmarkService {
    
    private BookmarkRepository repository;
    
    @Autowired
    public void setRepository(BookmarkRepository repository) {
        this.repository = repository;
    }
}
```

**使用场景：** 可选依赖

---

## 5. Bean的作用域

### 作用域类型

```java
@Service
@Scope("singleton")  // 默认，单例
public class BookmarkService {
    // 整个应用只有一个实例
}

@Component
@Scope("prototype")  // 原型，每次获取都创建新实例
public class BookmarkExporter {
    // 每次注入都是新对象
}

@Component
@Scope("request")  // Web：每个HTTP请求一个实例
public class RequestContext {
    // 请求结束后销毁
}

@Component
@Scope("session")  // Web：每个Session一个实例
public class UserSession {
    // Session结束后销毁
}
```

### 作用域对比

| 作用域 | 说明 | 使用场景 |
|-------|------|---------|
| `singleton` | 单例（默认） | 无状态的Service、Repository |
| `prototype` | 原型 | 有状态的对象，每次都需要新实例 |
| `request` | 请求 | 存储请求相关数据 |
| `session` | 会话 | 存储用户会话数据 |

---

## 6. 实战：书签系统的依赖注入

### 6.1 创建工具类Bean

```java
package com.example.bookmark.util;

import org.springframework.stereotype.Component;
import java.net.URL;

@Component
public class UrlValidator {
    
    public boolean isValid(String url) {
        try {
            new URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }
    
    public String normalize(String url) {
        return url.trim().toLowerCase();
    }
}
```

### 6.2 创建验证器Bean

```java
package com.example.bookmark.validator;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.util.UrlValidator;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookmarkValidator {
    
    private final UrlValidator urlValidator;  // 注入UrlValidator
    
    public void validate(Bookmark bookmark) {
        if (bookmark.getTitle() == null || bookmark.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        
        if (bookmark.getUrl() == null || bookmark.getUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("URL不能为空");
        }
        
        if (!urlValidator.isValid(bookmark.getUrl())) {
            throw new IllegalArgumentException("URL格式不正确");
        }
    }
}
```

### 6.3 在Service中使用

```java
package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.validator.BookmarkValidator;
import com.example.bookmark.util.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    
    // 依赖注入
    private final BookmarkRepository repository;
    private final BookmarkValidator validator;
    private final UrlValidator urlValidator;
    
    public Bookmark createBookmark(Bookmark bookmark) {
        // 验证
        validator.validate(bookmark);
        
        // 标准化URL
        String normalizedUrl = urlValidator.normalize(bookmark.getUrl());
        bookmark.setUrl(normalizedUrl);
        
        // 检查重复
        if (repository.existsByUrl(normalizedUrl)) {
            throw new IllegalArgumentException("URL已存在");
        }
        
        return repository.save(bookmark);
    }
    
    // 其他方法...
}
```

---

## 7. @Qualifier - 指定注入的Bean

### 场景：多个相同类型的Bean

```java
// 定义接口
public interface NotificationService {
    void send(String message);
}

// 实现1：邮件通知
@Service
@Qualifier("email")
public class EmailNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("发送邮件: " + message);
    }
}

// 实现2：短信通知
@Service
@Qualifier("sms")
public class SmsNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("发送短信: " + message);
    }
}

// 使用：指定注入哪个Bean
@Service
public class BookmarkService {
    
    private final NotificationService emailService;
    private final NotificationService smsService;
    
    public BookmarkService(
        @Qualifier("email") NotificationService emailService,
        @Qualifier("sms") NotificationService smsService
    ) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    
    public void notifyUser(String message) {
        emailService.send(message);
        smsService.send(message);
    }
}
```

---

## 8. @Primary - 默认Bean

```java
// 标记为主要Bean
@Service
@Primary
public class EmailNotificationService implements NotificationService {
    // ...
}

@Service
public class SmsNotificationService implements NotificationService {
    // ...
}

// 不指定@Qualifier时，注入@Primary的Bean
@Service
public class BookmarkService {
    
    private final NotificationService notificationService;  // 注入EmailNotificationService
    
    public BookmarkService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

---

## 9. @Conditional - 条件创建Bean

```java
@Configuration
public class CacheConfig {
    
    // 只在开发环境创建
    @Bean
    @Profile("dev")
    public CacheManager devCacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    // 只在生产环境创建
    @Bean
    @Profile("prod")
    public CacheManager prodCacheManager() {
        return new RedisCacheManager();
    }
    
    // 根据配置属性创建
    @Bean
    @ConditionalOnProperty(name = "feature.cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    // 当某个类存在时创建
    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    public RedisService redisService() {
        return new RedisService();
    }
}
```

---

## 10. Bean的初始化和销毁

### 方式1：注解

```java
@Component
public class DatabaseConnection {
    
    @PostConstruct  // 初始化方法
    public void init() {
        System.out.println("连接数据库...");
    }
    
    @PreDestroy  // 销毁方法
    public void cleanup() {
        System.out.println("关闭数据库连接...");
    }
}
```

### 方式2：@Bean注解

```java
@Configuration
public class AppConfig {
    
    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public DatabaseConnection databaseConnection() {
        return new DatabaseConnection();
    }
}
```

### 方式3：实现接口

```java
@Component
public class DatabaseConnection implements InitializingBean, DisposableBean {
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化...");
    }
    
    @Override
    public void destroy() throws Exception {
        System.out.println("销毁...");
    }
}
```

---

## 11. 实战练习

### 练习1：创建书签导出服务

```java
// 导出接口
public interface BookmarkExporter {
    String export(List<Bookmark> bookmarks);
}

// CSV导出实现
@Service
@Qualifier("csv")
public class CsvBookmarkExporter implements BookmarkExporter {
    @Override
    public String export(List<Bookmark> bookmarks) {
        // 实现CSV导出
        return "CSV格式";
    }
}

// JSON导出实现
@Service
@Qualifier("json")
public class JsonBookmarkExporter implements BookmarkExporter {
    @Override
    public String export(List<Bookmark> bookmarks) {
        // 实现JSON导出
        return "JSON格式";
    }
}

// 在Service中使用
@Service
@RequiredArgsConstructor
public class BookmarkService {
    
    @Qualifier("csv")
    private final BookmarkExporter csvExporter;
    
    @Qualifier("json")
    private final BookmarkExporter jsonExporter;
    
    public String exportToCsv(List<Bookmark> bookmarks) {
        return csvExporter.export(bookmarks);
    }
    
    public String exportToJson(List<Bookmark> bookmarks) {
        return jsonExporter.export(bookmarks);
    }
}
```

---

## 12. 常见问题

### Q1: 循环依赖怎么办？
**A:** 
```java
// ❌ 循环依赖
@Service
public class ServiceA {
    @Autowired
    private ServiceB serviceB;
}

@Service
public class ServiceB {
    @Autowired
    private ServiceA serviceA;
}

// ✅ 解决方案1：重构代码，提取公共逻辑
@Service
public class CommonService {
    // 公共逻辑
}

// ✅ 解决方案2：使用@Lazy延迟加载
@Service
public class ServiceA {
    private final ServiceB serviceB;
    
    public ServiceA(@Lazy ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}
```

### Q2: 什么时候用@Component，什么时候用@Bean？
**A:**
- **@Component**：自己写的类
- **@Bean**：第三方类或需要复杂配置的类

```java
// 自己的类 - 用@Component
@Component
public class MyService {
}

// 第三方类 - 用@Bean
@Configuration
public class Config {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

### Q3: 单例Bean线程安全吗？
**A:** 
- **无状态Bean**：线程安全（推荐）
- **有状态Bean**：不安全，需要同步或改用prototype

```java
// ✅ 线程安全（无状态）
@Service
public class BookmarkService {
    private final BookmarkRepository repository;  // 依赖注入，不可变
}

// ❌ 线程不安全（有状态）
@Service
public class BookmarkService {
    private int counter = 0;  // 共享状态
    
    public void increment() {
        counter++;  // 多线程不安全
    }
}
```

---

## 📝 今日总结

你已经学会了：
- ✅ 理解依赖注入和IoC的概念
- ✅ 使用@Component、@Service等注解创建Bean
- ✅ 掌握构造函数注入（推荐方式）
- ✅ 理解Bean的作用域
- ✅ 使用@Qualifier和@Primary
- ✅ Bean的初始化和销毁
- ✅ 解决循环依赖问题

**下一步：** [08-Spring MVC基础.md](./08-Spring-MVC基础.md) - 深入学习Web层开发
