# Day 3: Java异常处理与文件操作

> 前端类比：Java的try-catch ≈ JS的try-catch，但更严格

## 1. 异常处理基础

### 前端对比
```javascript
// JavaScript - 异常处理是可选的
try {
  const data = JSON.parse(invalidJson);
} catch (error) {
  console.error("解析失败:", error.message);
} finally {
  console.log("清理资源");
}

// 抛出异常
throw new Error("出错了");
```

### Java实现
```java
// Java - 某些异常必须处理
try {
    int result = 10 / 0;  // 会抛出ArithmeticException
} catch (ArithmeticException e) {
    System.err.println("除零错误: " + e.getMessage());
} finally {
    System.out.println("无论如何都会执行");
}

// 抛出异常
throw new IllegalArgumentException("参数不合法");
```

### 异常类型

```java
// 1. 检查型异常（Checked Exception）- 必须处理
try {
    FileReader file = new FileReader("file.txt");  // 可能抛出FileNotFoundException
} catch (FileNotFoundException e) {
    e.printStackTrace();
}

// 2. 运行时异常（Runtime Exception）- 可选处理
String str = null;
str.length();  // NullPointerException - 不强制捕获

// 3. 错误（Error）- 不应该捕获
// OutOfMemoryError, StackOverflowError等
```

### 💡 关键点
- **检查型异常**：编译器强制处理（如IOException）
- **运行时异常**：可选处理（如NullPointerException）
- **finally块**：无论是否异常都会执行，常用于资源清理

---

## 2. 异常处理最佳实践

### 多个catch块
```java
try {
    String str = "abc";
    int num = Integer.parseInt(str);  // NumberFormatException
    int result = 10 / num;            // ArithmeticException
} catch (NumberFormatException e) {
    System.err.println("数字格式错误: " + e.getMessage());
} catch (ArithmeticException e) {
    System.err.println("算术错误: " + e.getMessage());
} catch (Exception e) {
    // 捕获所有其他异常（放在最后）
    System.err.println("未知错误: " + e.getMessage());
}
```

### try-with-resources（推荐）
```java
// 旧写法 - 需要手动关闭资源
BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader("file.txt"));
    String line = reader.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 新写法 - 自动关闭资源（Java 7+）
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line = reader.readLine();
} catch (IOException e) {
    e.printStackTrace();
}
```

### 自定义异常
```java
// 定义自定义异常
public class BookmarkNotFoundException extends RuntimeException {
    public BookmarkNotFoundException(Long id) {
        super("找不到ID为 " + id + " 的书签");
    }
}

// 使用
public Bookmark getBookmarkById(Long id) {
    Bookmark bookmark = findById(id);
    if (bookmark == null) {
        throw new BookmarkNotFoundException(id);
    }
    return bookmark;
}
```

---

## 3. 在书签服务中使用异常

更新 `BookmarkService.java`：

```java
package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkService {
    
    private List<Bookmark> bookmarks = new ArrayList<>();
    private Long nextId = 1L;
    
    // 添加书签 - 参数验证
    public Bookmark addBookmark(String title, String url, String category) {
        // 参数验证
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL不能为空");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL必须以http://或https://开头");
        }
        
        Bookmark bookmark = new Bookmark(title, url, category);
        bookmark.setId(nextId++);
        bookmarks.add(bookmark);
        return bookmark;
    }
    
    // 根据ID查找 - 找不到抛异常
    public Bookmark getBookmarkById(Long id) {
        return bookmarks.stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new BookmarkNotFoundException(id));
    }
    
    // 更新书签
    public Bookmark updateBookmark(Long id, String title, String url, String category) {
        Bookmark bookmark = getBookmarkById(id);  // 找不到会抛异常
        
        if (title != null && !title.trim().isEmpty()) {
            bookmark.setTitle(title);
        }
        if (url != null && !url.trim().isEmpty()) {
            bookmark.setUrl(url);
        }
        if (category != null) {
            bookmark.setCategory(category);
        }
        
        return bookmark;
    }
    
    // 删除书签
    public void deleteBookmark(Long id) {
        Bookmark bookmark = getBookmarkById(id);  // 确保存在
        bookmarks.remove(bookmark);
    }
    
    // 其他方法...
}

// 自定义异常类
class BookmarkNotFoundException extends RuntimeException {
    public BookmarkNotFoundException(Long id) {
        super("找不到ID为 " + id + " 的书签");
    }
}
```

---

## 4. 文件操作

### 读取文件

```java
import java.io.*;
import java.nio.file.*;
import java.util.List;

// 方式1：逐行读取（适合大文件）
try (BufferedReader reader = new BufferedReader(new FileReader("bookmarks.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// 方式2：一次读取所有行（适合小文件）
try {
    List<String> lines = Files.readAllLines(Paths.get("bookmarks.txt"));
    lines.forEach(System.out::println);
} catch (IOException e) {
    e.printStackTrace();
}

// 方式3：读取为字符串
try {
    String content = new String(Files.readAllBytes(Paths.get("bookmarks.txt")));
    System.out.println(content);
} catch (IOException e) {
    e.printStackTrace();
}
```

### 写入文件

```java
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

// 方式1：逐行写入
try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
    writer.write("第一行");
    writer.newLine();
    writer.write("第二行");
} catch (IOException e) {
    e.printStackTrace();
}

// 方式2：一次写入所有行
List<String> lines = Arrays.asList("第一行", "第二行", "第三行");
try {
    Files.write(Paths.get("output.txt"), lines);
} catch (IOException e) {
    e.printStackTrace();
}

// 方式3：追加内容
try {
    Files.write(
        Paths.get("output.txt"), 
        "追加的内容\n".getBytes(),
        StandardOpenOption.APPEND
    );
} catch (IOException e) {
    e.printStackTrace();
}
```

---

## 5. 实战：书签导入导出

为BookmarkService添加导入导出功能：

```java
package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class BookmarkService {
    
    private List<Bookmark> bookmarks = new ArrayList<>();
    private Long nextId = 1L;
    
    // ... 其他方法
    
    /**
     * 导出书签到CSV文件
     * 格式: id,title,url,category
     */
    public void exportToCSV(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入表头
            writer.write("id,title,url,category");
            writer.newLine();
            
            // 写入数据
            for (Bookmark bookmark : bookmarks) {
                String line = String.format("%d,%s,%s,%s",
                    bookmark.getId(),
                    escapeCSV(bookmark.getTitle()),
                    escapeCSV(bookmark.getUrl()),
                    escapeCSV(bookmark.getCategory())
                );
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("导出成功: " + filePath);
    }
    
    /**
     * 从CSV文件导入书签
     */
    public void importFromCSV(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        // 跳过表头
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");
            
            if (parts.length >= 4) {
                String title = parts[1];
                String url = parts[2];
                String category = parts[3];
                
                try {
                    addBookmark(title, url, category);
                } catch (IllegalArgumentException e) {
                    System.err.println("跳过无效行: " + line);
                }
            }
        }
        System.out.println("导入成功: " + (lines.size() - 1) + " 条记录");
    }
    
    /**
     * 导出为JSON格式（简单实现）
     */
    public void exportToJSON(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("[\n");
            
            for (int i = 0; i < bookmarks.size(); i++) {
                Bookmark b = bookmarks.get(i);
                writer.write("  {\n");
                writer.write("    \"id\": " + b.getId() + ",\n");
                writer.write("    \"title\": \"" + escapeJSON(b.getTitle()) + "\",\n");
                writer.write("    \"url\": \"" + escapeJSON(b.getUrl()) + "\",\n");
                writer.write("    \"category\": \"" + escapeJSON(b.getCategory()) + "\"\n");
                writer.write("  }");
                
                if (i < bookmarks.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            writer.write("]\n");
        }
        System.out.println("导出JSON成功: " + filePath);
    }
    
    /**
     * 备份书签到文件
     */
    public void backup(String backupDir) throws IOException {
        // 创建备份目录
        Files.createDirectories(Paths.get(backupDir));
        
        // 生成带时间戳的文件名
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "bookmarks_backup_" + timestamp + ".csv";
        String filePath = backupDir + File.separator + fileName;
        
        exportToCSV(filePath);
    }
    
    // CSV转义（处理逗号和引号）
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    // JSON转义
    private String escapeJSON(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
```

---

## 6. 测试导入导出

创建测试类：

```java
package com.example.bookmark;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkService;
import org.junit.jupiter.api.Test;
import java.io.IOException;

public class BookmarkIOTest {
    
    @Test
    public void testExportImport() {
        BookmarkService service = new BookmarkService();
        
        // 添加测试数据
        service.addBookmark("Google", "https://google.com", "搜索引擎");
        service.addBookmark("GitHub", "https://github.com", "开发工具");
        service.addBookmark("StackOverflow", "https://stackoverflow.com", "开发工具");
        
        try {
            // 导出CSV
            service.exportToCSV("bookmarks.csv");
            
            // 导出JSON
            service.exportToJSON("bookmarks.json");
            
            // 备份
            service.backup("./backups");
            
            // 创建新服务实例并导入
            BookmarkService newService = new BookmarkService();
            newService.importFromCSV("bookmarks.csv");
            
            System.out.println("\n导入后的书签:");
            newService.getAllBookmarks().forEach(System.out::println);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testExceptionHandling() {
        BookmarkService service = new BookmarkService();
        
        // 测试参数验证
        try {
            service.addBookmark("", "https://google.com", "搜索");
        } catch (IllegalArgumentException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }
        
        try {
            service.addBookmark("Google", "invalid-url", "搜索");
        } catch (IllegalArgumentException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }
        
        // 测试找不到书签
        try {
            service.getBookmarkById(999L);
        } catch (BookmarkNotFoundException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }
    }
}
```

---

## 7. 常见问题

### Q1: 什么时候用检查型异常，什么时候用运行时异常？
**A:**
- **检查型异常**：可恢复的错误（如文件不存在，可以提示用户）
- **运行时异常**：编程错误（如空指针，应该修复代码）

```java
// 检查型异常 - 强制处理
public void readFile(String path) throws IOException {
    Files.readAllLines(Paths.get(path));
}

// 运行时异常 - 可选处理
public void validateInput(String input) {
    if (input == null) {
        throw new IllegalArgumentException("输入不能为null");
    }
}
```

### Q2: 应该捕获Exception还是具体的异常？
**A:** 优先捕获具体异常：
```java
// ❌ 不推荐 - 太宽泛
try {
    // ...
} catch (Exception e) {
    e.printStackTrace();
}

// ✅ 推荐 - 具体处理
try {
    // ...
} catch (FileNotFoundException e) {
    System.err.println("文件不存在");
} catch (IOException e) {
    System.err.println("IO错误");
}
```

### Q3: 什么时候用throws，什么时候用try-catch？
**A:**
- **throws**：让调用者处理异常
- **try-catch**：在当前方法处理异常

```java
// 方式1: 抛给调用者
public void method1() throws IOException {
    Files.readAllLines(Paths.get("file.txt"));
}

// 方式2: 自己处理
public void method2() {
    try {
        Files.readAllLines(Paths.get("file.txt"));
    } catch (IOException e) {
        System.err.println("读取失败");
    }
}
```

### Q4: 为什么要用try-with-resources？
**A:** 自动关闭资源，避免内存泄漏：
```java
// ❌ 容易忘记关闭
FileReader reader = new FileReader("file.txt");
// ... 如果这里抛异常，reader不会被关闭

// ✅ 自动关闭
try (FileReader reader = new FileReader("file.txt")) {
    // ... 无论如何都会关闭reader
}
```

---

## 8. 练习题

### 练习1：实现书签验证
为Bookmark类添加验证方法：
```java
public void validate() {
    // 验证title、url、category
    // 不合法则抛出IllegalArgumentException
}
```

### 练习2：实现书签搜索历史
实现一个类，记录搜索历史到文件：
```java
public class SearchHistory {
    public void addSearch(String keyword) throws IOException {
        // 追加到search_history.txt
    }
    
    public List<String> getRecentSearches(int limit) throws IOException {
        // 读取最近的N条搜索记录
    }
}
```

---

## 📝 今日总结

你已经学会了：
- ✅ 使用try-catch处理异常
- ✅ 区分检查型异常和运行时异常
- ✅ 创建自定义异常
- ✅ 使用try-with-resources管理资源
- ✅ 读写文件
- ✅ 实现书签的导入导出功能

**下一步：** [04-Maven快速入门.md](./04-Maven快速入门.md) - 学习Maven项目管理
