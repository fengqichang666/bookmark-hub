# Day 1-3: Java基础语法

> 前端类比：Java的类 ≈ TypeScript的class + interface

## 1. 类与对象（Class & Object）

### 前端对比
```javascript
// JavaScript/TypeScript
class Bookmark {
  constructor(title, url) {
    this.title = title;
    this.url = url;
  }
  
  display() {
    console.log(`${this.title}: ${this.url}`);
  }
}

const bookmark = new Bookmark("Google", "https://google.com");
```

### Java实现
```java
// Java - 强类型，需要声明类型
public class Bookmark {
    // 成员变量（属性）- 必须声明类型
    private String title;
    private String url;
    
    // 构造函数
    public Bookmark(String title, String url) {
        this.title = title;
        this.url = url;
    }
    
    // Getter方法（Java规范）
    public String getTitle() {
        return title;
    }
    
    public String getUrl() {
        return url;
    }
    
    // Setter方法
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    // 普通方法
    public void display() {
        System.out.println(title + ": " + url);
    }
}

// 使用
Bookmark bookmark = new Bookmark("Google", "https://google.com");
bookmark.display();
```

### 💡 关键点
- `private` = 私有属性，外部不能直接访问
- `public` = 公开方法，外部可以调用
- Java规范：用getter/setter访问私有属性
- `void` = 无返回值（类似JS的undefined）

### 🎯 实战：创建书签类

在你的项目中创建 `src/main/java/com/example/bookmark/model/Bookmark.java`：

```java
package com.example.bookmark.model;

import java.time.LocalDateTime;

public class Bookmark {
    private Long id;
    private String title;
    private String url;
    private String category;
    private LocalDateTime createdAt;
    
    // 构造函数
    public Bookmark() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Bookmark(String title, String url, String category) {
        this.title = title;
        this.url = url;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // toString方法 - 用于打印对象
    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
```

---

## 2. 接口（Interface）

### 前端对比
```typescript
// TypeScript接口
interface BookmarkService {
  addBookmark(bookmark: Bookmark): void;
  getBookmarks(): Bookmark[];
}

// 实现接口
class BookmarkServiceImpl implements BookmarkService {
  addBookmark(bookmark: Bookmark): void {
    // 实现
  }
  
  getBookmarks(): Bookmark[] {
    // 实现
    return [];
  }
}
```

### Java实现
```java
// 定义接口
public interface BookmarkService {
    void addBookmark(Bookmark bookmark);
    List<Bookmark> getBookmarks();
    Bookmark getBookmarkById(Long id);
    void deleteBookmark(Long id);
}

// 实现接口
public class BookmarkServiceImpl implements BookmarkService {
    
    @Override
    public void addBookmark(Bookmark bookmark) {
        // 实现逻辑
    }
    
    @Override
    public List<Bookmark> getBookmarks() {
        // 实现逻辑
        return new ArrayList<>();
    }
    
    @Override
    public Bookmark getBookmarkById(Long id) {
        // 实现逻辑
        return null;
    }
    
    @Override
    public void deleteBookmark(Long id) {
        // 实现逻辑
    }
}
```

### 💡 关键点
- 接口只定义方法签名，不实现
- 实现类必须实现接口的所有方法
- `@Override` 注解表示重写方法（可选但推荐）
- 接口用于定义契约，实现类提供具体逻辑

---

## 3. 泛型（Generics）

### 前端对比
```typescript
// TypeScript泛型
function getFirst<T>(list: T[]): T {
  return list[0];
}

const first = getFirst<string>(["a", "b", "c"]); // "a"
```

### Java实现
```java
// Java泛型
public <T> T getFirst(List<T> list) {
    return list.get(0);
}

// 使用
List<String> names = Arrays.asList("Alice", "Bob");
String first = getFirst(names); // "Alice"

// 泛型类
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String value = stringBox.get();
```

### 💡 关键点
- `<T>` 是类型参数，T可以是任何类型
- 常见泛型：`List<String>`, `Map<String, Integer>`
- 泛型提供编译时类型安全

---

## 4. 常用数据类型

### 基本类型对比

| Java类型 | 前端类型 | 说明 | 示例 |
|---------|---------|------|------|
| `int` | `number` | 整数 | `int age = 25;` |
| `long` | `number` | 长整数 | `long id = 123456789L;` |
| `double` | `number` | 浮点数 | `double price = 9.99;` |
| `boolean` | `boolean` | 布尔值 | `boolean active = true;` |
| `char` | `string` | 单字符 | `char grade = 'A';` |
| `String` | `string` | 字符串 | `String name = "Alice";` |

### 包装类型
```java
// 基本类型 vs 包装类型
int age = 25;           // 基本类型，不能为null
Integer ageObj = 25;    // 包装类型，可以为null

// 自动装箱/拆箱
Integer num = 100;      // 自动装箱：int -> Integer
int value = num;        // 自动拆箱：Integer -> int
```

### 💡 关键点
- 基本类型：性能高，不能为null
- 包装类型：可以为null，用于集合
- Spring Boot中推荐用包装类型（Long, Integer等）

---

## 5. 字符串操作

### 前端对比
```javascript
// JavaScript
const str = "Hello World";
const upper = str.toUpperCase();
const contains = str.includes("World");
const parts = str.split(" ");
```

### Java实现
```java
// Java
String str = "Hello World";
String upper = str.toUpperCase();
boolean contains = str.contains("World");
String[] parts = str.split(" ");

// 字符串拼接
String name = "Alice";
String greeting = "Hello, " + name;  // 方式1
String greeting2 = String.format("Hello, %s", name);  // 方式2

// 字符串比较（重要！）
String a = "hello";
String b = "hello";
boolean equal = a.equals(b);  // ✅ 正确：比较内容
boolean wrong = (a == b);     // ❌ 错误：比较引用地址
```

### 💡 关键点
- **永远用 `.equals()` 比较字符串，不要用 `==`**
- `==` 比较的是对象引用，不是内容
- 字符串是不可变的（immutable）

---

## 6. 实战练习

### 练习1：创建书签类并测试

创建 `src/test/java/com/example/bookmark/BookmarkTest.java`：

```java
package com.example.bookmark;

import com.example.bookmark.model.Bookmark;
import org.junit.jupiter.api.Test;

public class BookmarkTest {
    
    @Test
    public void testCreateBookmark() {
        // 创建书签
        Bookmark bookmark = new Bookmark(
            "Google", 
            "https://google.com", 
            "搜索引擎"
        );
        
        // 打印信息
        System.out.println(bookmark);
        System.out.println("标题: " + bookmark.getTitle());
        System.out.println("URL: " + bookmark.getUrl());
        System.out.println("分类: " + bookmark.getCategory());
        
        // 修改信息
        bookmark.setTitle("Google搜索");
        System.out.println("修改后: " + bookmark.getTitle());
    }
}
```

运行测试：
```bash
mvn test
```

### 练习2：实现书签比较

为Bookmark类添加比较方法：

```java
public class Bookmark {
    // ... 其他代码
    
    // 判断两个书签是否相同（根据URL）
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Bookmark bookmark = (Bookmark) obj;
        return url != null && url.equals(bookmark.url);
    }
    
    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
```

---

## 7. 常见问题

### Q1: 为什么要写这么多getter/setter？
**A:** Java规范要求，但可以用Lombok简化：
```java
import lombok.Data;

@Data  // 自动生成getter/setter/toString等
public class Bookmark {
    private String title;
    private String url;
}
```

### Q2: public/private/protected有什么区别？
**A:** 访问修饰符：
- `public`: 任何地方都能访问
- `private`: 只能在类内部访问
- `protected`: 类内部 + 子类可访问
- 默认（不写）: 同包内可访问

### Q3: String和StringBuilder有什么区别？
**A:** 
- `String`: 不可变，每次修改都创建新对象
- `StringBuilder`: 可变，适合频繁拼接字符串
```java
// 不推荐：循环中拼接String
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // 每次都创建新对象
}

// 推荐：用StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

---

## 📝 今日总结

你已经学会了：
- ✅ 创建Java类和对象
- ✅ 定义和实现接口
- ✅ 使用泛型
- ✅ Java基本数据类型
- ✅ 字符串操作
- ✅ 创建书签模型类

**下一步：** [02-Java集合与Lambda.md](./02-Java集合与Lambda.md) - 学习List、Map和Stream操作
