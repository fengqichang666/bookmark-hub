# Day 4-5: Maven快速入门

> 前端类比：Maven ≈ npm + package.json + webpack

## 1. Maven是什么？

### 前端对比

| 功能 | 前端 | Maven |
|------|------|-------|
| 依赖管理 | package.json | pom.xml |
| 安装依赖 | `npm install` | `mvn install` |
| 运行脚本 | `npm run build` | `mvn package` |
| 依赖仓库 | npmjs.com | Maven Central |
| 本地缓存 | node_modules | ~/.m2/repository |

### Maven的作用
1. **依赖管理** - 自动下载和管理第三方库
2. **项目构建** - 编译、测试、打包
3. **项目结构** - 标准化的目录结构
4. **插件系统** - 扩展构建功能

---

## 2. Maven项目结构

### 标准目录结构
```
bookmark-hub/
├── pom.xml                    # 项目配置文件（类似package.json）
├── src/
│   ├── main/
│   │   ├── java/             # Java源代码
│   │   │   └── com/example/bookmark/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       └── controller/
│   │   └── resources/        # 配置文件、静态资源
│   │       ├── application.yml
│   │       └── static/
│   └── test/
│       ├── java/             # 测试代码
│       └── resources/        # 测试资源
└── target/                   # 编译输出目录（类似dist/）
    ├── classes/
    └── bookmark-hub-1.0.0.jar
```

### 前端对比
```
前端项目结构              Maven项目结构
├── package.json      →  ├── pom.xml
├── src/              →  ├── src/main/java/
├── test/             →  ├── src/test/java/
├── dist/             →  ├── target/
└── node_modules/     →  └── ~/.m2/repository/
```

---

## 3. pom.xml详解

### 基础结构
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    
    <!-- 项目坐标（唯一标识） -->
    <groupId>com.example</groupId>           <!-- 组织/公司域名倒写 -->
    <artifactId>bookmark-hub</artifactId>    <!-- 项目名称 -->
    <version>1.0.0</version>                 <!-- 版本号 -->
    <packaging>jar</packaging>               <!-- 打包方式：jar或war -->
    
    <!-- 项目信息 -->
    <name>Bookmark Hub</name>
    <description>书签管理系统</description>
    
    <!-- 属性配置 -->
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <!-- 依赖 -->
    <dependencies>
        <!-- 这里添加依赖 -->
    </dependencies>
    
    <!-- 构建配置 -->
    <build>
        <plugins>
            <!-- 这里添加插件 -->
        </plugins>
    </build>
</project>
```

### 前端对比
```json
// package.json
{
  "name": "bookmark-hub",           // ≈ artifactId
  "version": "1.0.0",               // ≈ version
  "description": "书签管理系统",     // ≈ description
  "main": "index.js",
  "scripts": {                      // ≈ Maven插件
    "build": "webpack",
    "test": "jest"
  },
  "dependencies": {                 // ≈ Maven dependencies
    "express": "^4.18.0"
  },
  "devDependencies": {              // ≈ Maven dependencies with scope=test
    "jest": "^29.0.0"
  }
}
```

---

## 4. 添加依赖

### 前端对比
```bash
# npm
npm install express
npm install --save-dev jest
```

```json
// package.json
{
  "dependencies": {
    "express": "^4.18.0"
  },
  "devDependencies": {
    "jest": "^29.0.0"
  }
}
```

### Maven实现
```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>
    
    <!-- JUnit 5 测试框架 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>  <!-- 只在测试时使用 -->
    </dependency>
    
    <!-- Lombok - 简化代码 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>  <!-- 编译时使用，运行时不需要 -->
    </dependency>
</dependencies>
```

### 依赖坐标（GAV）
```xml
<dependency>
    <groupId>组织名</groupId>        <!-- G: Group -->
    <artifactId>项目名</artifactId>  <!-- A: Artifact -->
    <version>版本号</version>        <!-- V: Version -->
</dependency>
```

### 依赖范围（scope）

| scope | 说明 | 前端类比 |
|-------|------|---------|
| `compile` | 默认，编译+运行都需要 | `dependencies` |
| `test` | 只在测试时需要 | `devDependencies` |
| `provided` | 编译时需要，运行时由容器提供 | `peerDependencies` |
| `runtime` | 运行时需要，编译时不需要 | - |

---

## 5. 查找依赖

### 方式1：Maven Central Repository
访问 https://mvnrepository.com/

搜索 "spring boot web"，复制依赖配置：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.0</version>
</dependency>
```

### 方式2：IntelliJ IDEA
1. 打开pom.xml
2. 在`<dependencies>`标签内按 `Alt + Insert`
3. 选择 "Add Dependency"
4. 搜索并选择依赖

---

## 6. Maven命令

### 常用命令对比

| Maven命令 | 前端对比 | 说明 |
|----------|---------|------|
| `mvn clean` | `rm -rf dist/` | 清理编译输出 |
| `mvn compile` | `tsc` / `babel` | 编译源代码 |
| `mvn test` | `npm test` | 运行测试 |
| `mvn package` | `npm run build` | 打包成jar/war |
| `mvn install` | `npm install` | 安装到本地仓库 |
| `mvn dependency:tree` | `npm list` | 查看依赖树 |

### 命令详解

```bash
# 1. 清理项目（删除target目录）
mvn clean

# 2. 编译项目
mvn compile

# 3. 运行测试
mvn test

# 4. 打包项目（生成jar或war）
mvn package

# 5. 安装到本地仓库
mvn install

# 6. 组合命令（常用）
mvn clean package        # 清理并打包
mvn clean install        # 清理、打包并安装
mvn clean test          # 清理并测试

# 7. 跳过测试
mvn package -DskipTests

# 8. 查看依赖树
mvn dependency:tree

# 9. 下载依赖源码
mvn dependency:sources
```

---

## 7. Maven生命周期

### 三个生命周期

```
1. clean生命周期
   └── clean (清理)

2. default生命周期（主要）
   ├── validate (验证)
   ├── compile (编译)
   ├── test (测试)
   ├── package (打包)
   ├── verify (验证)
   ├── install (安装到本地)
   └── deploy (部署到远程)

3. site生命周期
   └── site (生成项目站点)
```

### 前端对比
```json
// package.json scripts
{
  "scripts": {
    "clean": "rm -rf dist",           // ≈ mvn clean
    "compile": "tsc",                 // ≈ mvn compile
    "test": "jest",                   // ≈ mvn test
    "build": "webpack",               // ≈ mvn package
    "deploy": "npm publish"           // ≈ mvn deploy
  }
}
```

### 💡 关键点
- 执行后面的阶段会自动执行前面的阶段
- `mvn package` 会自动执行 compile → test → package
- `mvn clean package` 会先清理，再执行完整流程

---

## 8. 实战：创建书签项目的pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    
    <!-- 项目坐标 -->
    <groupId>com.example</groupId>
    <artifactId>bookmark-hub</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Bookmark Hub</name>
    <description>书签管理系统 - Java全栈学习项目</description>
    
    <!-- 属性配置 -->
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring.boot.version>3.2.0</spring.boot.version>
    </properties>
    
    <!-- 依赖管理 -->
    <dependencies>
        <!-- JUnit 5 测试 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
        
        <!-- Lombok - 简化代码 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        
        <!-- JSON处理 -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
    
    <!-- 构建配置 -->
    <build>
        <plugins>
            <!-- Maven编译插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            
            <!-- Maven测试插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 9. 配置国内镜像（加速下载）

### 方式1：全局配置（推荐）

编辑 `~/.m2/settings.xml`（Windows: `C:\Users\你的用户名\.m2\settings.xml`）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <!-- 配置阿里云镜像 -->
    <mirrors>
        <mirror>
            <id>aliyun-maven</id>
            <name>阿里云Maven中央仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

### 方式2：项目配置

在pom.xml中添加：

```xml
<repositories>
    <repository>
        <id>aliyun</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

### 前端对比
```bash
# npm配置淘宝镜像
npm config set registry https://registry.npmmirror.com
```

---

## 10. 常见问题

### Q1: Maven和Gradle有什么区别？
**A:**
- **Maven**: XML配置，约定优于配置，生态成熟
- **Gradle**: Groovy/Kotlin DSL，更灵活，构建更快

新手建议先学Maven，因为：
- 配置更直观（XML vs 脚本）
- 文档和教程更多
- Spring Boot默认用Maven

### Q2: 依赖冲突怎么办？
**A:** 使用依赖树查看：
```bash
mvn dependency:tree

# 排除冲突的依赖
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Q3: 为什么要用parent POM？
**A:** 继承公共配置，类似前端的extends：
```xml
<!-- 继承Spring Boot父POM -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<!-- 好处：不用写版本号 -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- 版本号由parent管理 -->
    </dependency>
</dependencies>
```

### Q4: target目录可以删除吗？
**A:** 可以，类似前端的dist/目录：
```bash
mvn clean  # 删除target目录
```

---

## 11. 实战练习

### 练习1：创建Maven项目
```bash
# 使用Maven命令创建项目
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=bookmark-hub \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd bookmark-hub
mvn clean package
```

### 练习2：添加依赖并测试
1. 在pom.xml中添加Gson依赖
2. 创建测试类使用Gson
3. 运行 `mvn test`

```java
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

public class GsonTest {
    @Test
    public void testGson() {
        Gson gson = new Gson();
        String json = gson.toJson(new Bookmark("Google", "https://google.com", "搜索"));
        System.out.println(json);
    }
}
```

---

## 📝 今日总结

你已经学会了：
- ✅ 理解Maven的作用和项目结构
- ✅ 编写和理解pom.xml
- ✅ 添加和管理依赖
- ✅ 使用Maven命令构建项目
- ✅ 理解Maven生命周期
- ✅ 配置国内镜像加速

**下一步：** [06-Spring Boot快速开始.md](./06-Spring-Boot快速开始.md) - 开始学习Spring Boot框架
