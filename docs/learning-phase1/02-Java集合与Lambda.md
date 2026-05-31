# Day 2: Java集合与Lambda表达式

> 前端类比：Java的List ≈ JS的Array，Map ≈ JS的Object/Map

## 1. List（列表）

### 前端对比
```javascript
// JavaScript Array
const bookmarks = [];
bookmarks.push({ title: "Google", url: "https://google.com" });
bookmarks.push({ title: "GitHub", url: "https://github.com" });

console.log(bookmarks.length);  // 2
console.log(bookmarks[0]);      // 第一个元素

// 遍历
bookmarks.forEach(b => console.log(b.title));

// 过滤
const filtered = bookmarks.filter(b => b.title.includes("G"));
```

### Java实现
```java
import java.util.ArrayList;
import java.util.List;

// 创建List
List<Bookmark> bookmarks = new ArrayList<>();

// 添加元素
bookmarks.add(new Bookmark("Google", "https://google.com", "搜索"));
bookmarks.add(new Bookmark("GitHub", "https://github.com", "开发"));

// 获取大小和元素
System.out.println(bookmarks.size());  // 2
System.out.println(bookmarks.get(0));  // 第一个元素

// 遍历方式1：传统for循环
for (int i = 0; i < bookmarks.size(); i++) {
    Bookmark b = bookmarks.get(i);
    System.out.println(b.getTitle());
}

// 遍历方式2：增强for循环（推荐）
for (Bookmark b : bookmarks) {
    System.out.println(b.getTitle());
}

// 遍历方式3：Lambda表达式（最现代）
bookmarks.forEach(b -> System.out.println(b.getTitle()));
```

### List常用方法

| 方法 | 说明 | 前端对比 |
|------|------|---------|
| `add(element)` | 添加元素 | `push()` |
| `get(index)` | 获取元素 | `[index]` |
| `remove(index)` | 删除元素 | `splice(index, 1)` |
| `size()` | 获取大小 | `length` |
| `contains(element)` | 是否包含 | `includes()` |
| `clear()` | 清空 | `length = 0` |
| `isEmpty()` | 是否为空 | `length === 0` |

### 💡 关键点
- `List` 是接口，`ArrayList` 是实现类
- 必须指定泛型：`List<Bookmark>`
- 索引从0开始，用 `get(index)` 访问

---

## 2. Map（映射）

### 前端对比
```javascript
// JavaScript Object/Map
const categoryMap = {
  "搜索": ["Google", "Bing"],
  "开发": ["GitHub", "StackOverflow"]
};

// 或使用Map
const map = new Map();
map.set("搜索", ["Google", "Bing"]);
map.set("开发", ["GitHub", "StackOverflow"]);

console.log(map.get("搜索"));  // ["Google", "Bing"]
console.log(map.has("搜索"));  // true
```

### Java实现
```java
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// 创建Map
Map<String, List<String>> categoryMap = new HashMap<>();

// 添加数据
List<String> searchEngines = new ArrayList<>();
searchEngines.add("Google");
searchEngines.add("Bing");
categoryMap.put("搜索", searchEngines);

List<String> devSites = new ArrayList<>();
devSites.add("GitHub");
devSites.add("StackOverflow");
categoryMap.put("开发", devSites);

// 获取数据
List<String> search = categoryMap.get("搜索");
System.out.println(search);  // [Google, Bing]

// 检查是否存在
boolean hasKey = categoryMap.containsKey("搜索");  // true
boolean hasValue = categoryMap.containsValue(searchEngines);  // true

// 遍历Map
for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
    String category = entry.getKey();
    List<String> sites = entry.getValue();
    System.out.println(category + ": " + sites);
}

// Lambda遍历（推荐）
categoryMap.forEach((category, sites) -> {
    System.out.println(category + ": " + sites);
});
```

### Map常用方法

| 方法 | 说明 | 前端对比 |
|------|------|---------|
| `put(key, value)` | 添加/更新 | `map.set()` 或 `obj[key] = value` |
| `get(key)` | 获取值 | `map.get()` 或 `obj[key]` |
| `remove(key)` | 删除 | `map.delete()` 或 `delete obj[key]` |
| `containsKey(key)` | 是否有key | `map.has()` 或 `key in obj` |
| `keySet()` | 所有key | `map.keys()` 或 `Object.keys()` |
| `values()` | 所有value | `map.values()` 或 `Object.values()` |
| `size()` | 大小 | `map.size` 或 `Object.keys().length` |

---

## 3. Lambda表达式

### 前端对比
```javascript
// JavaScript箭头函数
const numbers = [1, 2, 3, 4, 5];

// 过滤
const evens = numbers.filter(n => n % 2 === 0);

// 映射
const doubled = numbers.map(n => n * 2);

// 查找
const found = numbers.find(n => n > 3);

// 排序
numbers.sort((a, b) => a - b);
```

### Java实现
```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 过滤
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
// 结果: [2, 4]

// 映射
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
// 结果: [2, 4, 6, 8, 10]

// 查找
Integer found = numbers.stream()
    .filter(n -> n > 3)
    .findFirst()
    .orElse(null);
// 结果: 4

// 排序
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());
```

### Lambda语法

```java
// 完整写法
(参数) -> { 
    // 多行代码
    return 结果;
}

// 简化写法（单行）
(参数) -> 表达式

// 示例
// 无参数
() -> System.out.println("Hello")

// 单参数（可省略括号）
x -> x * 2
(x) -> x * 2  // 等价

// 多参数
(x, y) -> x + y

// 多行代码
(x, y) -> {
    int sum = x + y;
    return sum * 2;
}
```

---

## 4. Stream API（重点）

### 常用Stream操作

```java
import java.util.List;
import java.util.stream.Collectors;

List<Bookmark> bookmarks = getBookmarks();

// 1. filter - 过滤
List<Bookmark> devBookmarks = bookmarks.stream()
    .filter(b -> b.getCategory().equals("开发"))
    .collect(Collectors.toList());

// 2. map - 转换
List<String> titles = bookmarks.stream()
    .map(b -> b.getTitle())
    .collect(Collectors.toList());

// 简化写法（方法引用）
List<String> titles2 = bookmarks.stream()
    .map(Bookmark::getTitle)
    .collect(Collectors.toList());

// 3. sorted - 排序
List<Bookmark> sorted = bookmarks.stream()
    .sorted((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()))
    .collect(Collectors.toList());

// 4. limit - 限制数量
List<Bookmark> top5 = bookmarks.stream()
    .limit(5)
    .collect(Collectors.toList());

// 5. count - 计数
long count = bookmarks.stream()
    .filter(b -> b.getCategory().equals("开发"))
    .count();

// 6. anyMatch - 是否存在
boolean hasGoogle = bookmarks.stream()
    .anyMatch(b -> b.getTitle().contains("Google"));

// 7. 链式操作
List<String> result = bookmarks.stream()
    .filter(b -> b.getCategory().equals("开发"))
    .map(Bookmark::getTitle)
    .sorted()
    .limit(10)
    .collect(Collectors.toList());
```

### Stream操作对比表

| Stream方法 | 前端对比 | 说明 |
|-----------|---------|------|
| `filter()` | `filter()` | 过滤元素 |
| `map()` | `map()` | 转换元素 |
| `sorted()` | `sort()` | 排序 |
| `limit()` | `slice(0, n)` | 限制数量 |
| `skip()` | `slice(n)` | 跳过前n个 |
| `distinct()` | `[...new Set()]` | 去重 |
| `count()` | `length` | 计数 |
| `anyMatch()` | `some()` | 是否有满足条件 |
| `allMatch()` | `every()` | 是否全部满足 |
| `findFirst()` | `find()` | 查找第一个 |
| `forEach()` | `forEach()` | 遍历 |

---

## 5. 实战：书签服务类

创建 `src/main/java/com/example/bookmark/service/BookmarkService.java`：

```java
package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkService {
    
    // 用List存储书签（后面会改用数据库）
    private List<Bookmark> bookmarks = new ArrayList<>();
    private Long nextId = 1L;
    
    // 添加书签
    public Bookmark addBookmark(String title, String url, String category) {
        Bookmark bookmark = new Bookmark(title, url, category);
        bookmark.setId(nextId++);
        bookmarks.add(bookmark);
        return bookmark;
    }
    
    // 获取所有书签
    public List<Bookmark> getAllBookmarks() {
        return new ArrayList<>(bookmarks);  // 返回副本，防止外部修改
    }
    
    // 根据ID查找书签
    public Bookmark getBookmarkById(Long id) {
        return bookmarks.stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // 根据分类查找书签
    public List<Bookmark> getBookmarksByCategory(String category) {
        return bookmarks.stream()
            .filter(b -> b.getCategory().equals(category))
            .collect(Collectors.toList());
    }
    
    // 搜索书签（标题或URL包含关键词）
    public List<Bookmark> searchBookmarks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return bookmarks.stream()
            .filter(b -> 
                b.getTitle().toLowerCase().contains(lowerKeyword) ||
                b.getUrl().toLowerCase().contains(lowerKeyword)
            )
            .collect(Collectors.toList());
    }
    
    // 更新书签
    public boolean updateBookmark(Long id, String title, String url, String category) {
        Bookmark bookmark = getBookmarkById(id);
        if (bookmark != null) {
            bookmark.setTitle(title);
            bookmark.setUrl(url);
            bookmark.setCategory(category);
            return true;
        }
        return false;
    }
    
    // 删除书签
    public boolean deleteBookmark(Long id) {
        return bookmarks.removeIf(b -> b.getId().equals(id));
    }
    
    // 获取所有分类
    public List<String> getAllCategories() {
        return bookmarks.stream()
            .map(Bookmark::getCategory)
            .distinct()  // 去重
            .sorted()    // 排序
            .collect(Collectors.toList());
    }
    
    // 统计每个分类的书签数量
    public void printCategoryStats() {
        bookmarks.stream()
            .collect(Collectors.groupingBy(
                Bookmark::getCategory,
                Collectors.counting()
            ))
            .forEach((category, count) -> 
                System.out.println(category + ": " + count + "个书签")
            );
    }
}
```

---

## 6. 测试服务类

创建 `src/test/java/com/example/bookmark/BookmarkServiceTest.java`：

```java
package com.example.bookmark;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkService;
import org.junit.jupiter.api.Test;
import java.util.List;

public class BookmarkServiceTest {
    
    @Test
    public void testBookmarkService() {
        BookmarkService service = new BookmarkService();
        
        // 添加书签
        service.addBookmark("Google", "https://google.com", "搜索引擎");
        service.addBookmark("GitHub", "https://github.com", "开发工具");
        service.addBookmark("Bing", "https://bing.com", "搜索引擎");
        service.addBookmark("StackOverflow", "https://stackoverflow.com", "开发工具");
        
        // 获取所有书签
        System.out.println("=== 所有书签 ===");
        List<Bookmark> all = service.getAllBookmarks();
        all.forEach(System.out::println);
        
        // 按分类查找
        System.out.println("\n=== 搜索引擎分类 ===");
        List<Bookmark> searchEngines = service.getBookmarksByCategory("搜索引擎");
        searchEngines.forEach(b -> System.out.println(b.getTitle()));
        
        // 搜索
        System.out.println("\n=== 搜索'git' ===");
        List<Bookmark> searchResult = service.searchBookmarks("git");
        searchResult.forEach(b -> System.out.println(b.getTitle()));
        
        // 获取所有分类
        System.out.println("\n=== 所有分类 ===");
        List<String> categories = service.getAllCategories();
        categories.forEach(System.out::println);
        
        // 统计
        System.out.println("\n=== 分类统计 ===");
        service.printCategoryStats();
        
        // 更新书签
        System.out.println("\n=== 更新书签 ===");
        service.updateBookmark(1L, "Google搜索", "https://google.com", "搜索引擎");
        Bookmark updated = service.getBookmarkById(1L);
        System.out.println("更新后: " + updated.getTitle());
        
        // 删除书签
        System.out.println("\n=== 删除书签 ===");
        service.deleteBookmark(2L);
        System.out.println("删除后剩余: " + service.getAllBookmarks().size() + "个");
    }
}
```

运行测试：
```bash
mvn test -Dtest=BookmarkServiceTest
```

---

## 7. 常见问题

### Q1: List和ArrayList有什么区别？
**A:** 
- `List` 是接口（抽象），定义了列表的行为
- `ArrayList` 是实现类（具体），提供了具体实现
- 推荐写法：`List<String> list = new ArrayList<>();`（面向接口编程）

### Q2: 什么时候用List，什么时候用Set？
**A:**
- `List`: 有序，可重复，用于大多数场景
- `Set`: 无序，不可重复，用于去重
```java
List<String> list = Arrays.asList("a", "b", "a");  // [a, b, a]
Set<String> set = new HashSet<>(list);             // [a, b]
```

### Q3: Stream会修改原集合吗？
**A:** 不会！Stream是不可变的：
```java
List<Integer> numbers = Arrays.asList(1, 2, 3);
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

System.out.println(numbers);  // [1, 2, 3] - 原集合不变
System.out.println(doubled);  // [2, 4, 6] - 新集合
```

### Q4: 为什么要用 `.collect(Collectors.toList())`？
**A:** Stream是惰性的，需要终止操作才会执行：
```java
// 这段代码不会执行任何操作！
bookmarks.stream()
    .filter(b -> b.getCategory().equals("开发"))
    .map(Bookmark::getTitle);

// 必须加终止操作
List<String> titles = bookmarks.stream()
    .filter(b -> b.getCategory().equals("开发"))
    .map(Bookmark::getTitle)
    .collect(Collectors.toList());  // 终止操作
```

---

## 8. 练习题

### 练习1：实现书签排序
为BookmarkService添加方法，按创建时间倒序排列书签：
```java
public List<Bookmark> getBookmarksSortedByDate() {
    // 你的代码
}
```

<details>
<summary>点击查看答案</summary>

```java
public List<Bookmark> getBookmarksSortedByDate() {
    return bookmarks.stream()
        .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
        .collect(Collectors.toList());
}
```
</details>

### 练习2：统计URL域名
实现方法，统计每个域名有多少个书签：
```java
public Map<String, Long> countByDomain() {
    // 提示：从URL中提取域名，然后用groupingBy
}
```

---

## 📝 今日总结

你已经学会了：
- ✅ 使用List存储和操作数据
- ✅ 使用Map存储键值对
- ✅ 编写Lambda表达式
- ✅ 使用Stream API进行数据处理
- ✅ 实现完整的书签服务类

**下一步：** [03-Java异常与IO.md](./03-Java异常与IO.md) - 学习异常处理和文件操作
