# Bookmark Hub MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a runnable team bookmark management MVP with a React frontend, Spring Boot backend, MySQL persistence, login, permissions, category/bookmark/member management, and bookmark HTML import.

**Architecture:** The project is a monorepo with `frontend` and `backend` folders. The frontend is a React 19 SPA that talks to a Spring Boot 3 REST API secured with JWT. The backend persists MySQL data through JPA and Flyway, while automated tests use Vitest on the frontend and Spring Boot tests with H2 on the backend.

**Tech Stack:** React 19, Vite, TypeScript, Ant Design 5, React Router, TanStack Query, Zustand, Vitest, Spring Boot 3, Java 21, Spring Security, Spring Data JPA, Flyway, MySQL 8, H2, Jsoup

---

## Planned File Structure

### Root

- Create: `D:\workspace\bookmark-hub\.gitignore`
- Create: `D:\workspace\bookmark-hub\README.md`
- Create: `D:\workspace\bookmark-hub\frontend\`
- Create: `D:\workspace\bookmark-hub\backend\`

### Frontend

- Create: `D:\workspace\bookmark-hub\frontend\package.json`
- Create: `D:\workspace\bookmark-hub\frontend\vite.config.ts`
- Create: `D:\workspace\bookmark-hub\frontend\src\main.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\App.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\router\index.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\layouts\AppLayout.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\api\http.ts`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\auth\authStore.ts`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\auth\LoginPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\dashboard\DashboardPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\bookmarks\BookmarkPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\categories\CategoryPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\members\MemberPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\imports\ImportPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\test\setup.ts`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\auth\LoginPage.test.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\bookmarks\BookmarkPage.test.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\imports\ImportPage.test.tsx`

### Backend

- Create: `D:\workspace\bookmark-hub\backend\pom.xml`
- Create: `D:\workspace\bookmark-hub\backend\mvnw.cmd`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\BookmarkHubApplication.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\config\SecurityConfig.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\auth\AuthController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\auth\AuthService.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\bookmark\BookmarkController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\bookmark\BookmarkService.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\category\CategoryController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\member\TeamMemberController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\dashboard\DashboardController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\importing\ImportController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\resources\application.yml`
- Create: `D:\workspace\bookmark-hub\backend\src\main\resources\db\migration\V1__init_schema.sql`
- Create: `D:\workspace\bookmark-hub\backend\src\main\resources\db\migration\V2__seed_admin.sql`
- Create: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\auth\AuthControllerTest.java`
- Create: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\bookmark\BookmarkControllerTest.java`
- Create: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\category\CategoryControllerTest.java`
- Create: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\importing\ImportControllerTest.java`

### Docs

- Create: `D:\workspace\bookmark-hub\docs\superpowers\specs\2026-05-20-bookmark-hub-design.md`
- Create: `D:\workspace\bookmark-hub\docs\superpowers\plans\2026-05-20-bookmark-hub-implementation.md`

### Implementation Assumption

- Use `backend\mvnw.cmd` for all backend commands. This removes the dependency on a global Maven installation, which was not discoverable through `winget` during planning.

### Task 1: Bootstrap Workspace And Tooling

**Files:**
- Create: `D:\workspace\bookmark-hub\.gitignore`
- Create: `D:\workspace\bookmark-hub\README.md`

- [ ] **Step 1: Install missing runtime dependencies**

```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK -e --accept-package-agreements --accept-source-agreements
winget install --id Oracle.MySQL -e --accept-package-agreements --accept-source-agreements
```

- [ ] **Step 2: Verify the installed runtimes**

Run:

```powershell
java -version
mysql --version
```

Expected:

- `java -version` prints a Temurin 21 build
- `mysql --version` prints a MySQL 8 client version

- [ ] **Step 3: Initialize git and add the root ignore file**

```powershell
git init
@'
node_modules/
frontend/node_modules/
frontend/dist/
frontend/coverage/
backend/target/
.idea/
.vscode/
.superpowers/
*.log
.env
'@ | Set-Content -Path 'D:\workspace\bookmark-hub\.gitignore'
```

- [ ] **Step 4: Add the root README with local startup commands**

```markdown
# Bookmark Hub

## Local Development

### Frontend

`cd frontend`
`npm install`
`npm run dev`

### Backend

`cd backend`
`.\mvnw.cmd spring-boot:run`

### Default Accounts

- `admin` / `Admin@123456`
```

- [ ] **Step 5: Commit the workspace bootstrap**

```powershell
git add .gitignore README.md docs/superpowers/specs/2026-05-20-bookmark-hub-design.md docs/superpowers/plans/2026-05-20-bookmark-hub-implementation.md
git commit -m "chore: initialize bookmark hub workspace"
```

### Task 2: Scaffold The Frontend Shell

**Files:**
- Create: `D:\workspace\bookmark-hub\frontend\package.json`
- Create: `D:\workspace\bookmark-hub\frontend\src\main.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\App.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\router\index.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\layouts\AppLayout.tsx`
- Test: `D:\workspace\bookmark-hub\frontend\src\features\auth\LoginPage.test.tsx`

- [ ] **Step 1: Generate the Vite React TypeScript app and install core dependencies**

```powershell
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
npm install antd @ant-design/icons react-router-dom @tanstack/react-query zustand axios dayjs
npm install -D vitest @testing-library/react @testing-library/jest-dom @testing-library/user-event jsdom msw
```

- [ ] **Step 2: Write the first failing frontend test for the login screen**

```tsx
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { LoginPage } from './LoginPage';

test('renders username and password inputs on login page', () => {
  render(
    <MemoryRouter>
      <LoginPage />
    </MemoryRouter>,
  );

  expect(screen.getByLabelText('用户名')).toBeInTheDocument();
  expect(screen.getByLabelText('密码')).toBeInTheDocument();
});
```

- [ ] **Step 3: Run the test to verify it fails because the page does not exist yet**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npx vitest run src/features/auth/LoginPage.test.tsx
```

Expected:

- FAIL with module or component not found for `LoginPage`

- [ ] **Step 4: Add the router, query client, layout shell, and minimal login page**

```tsx
// D:\workspace\bookmark-hub\frontend\src\router\index.tsx
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppLayout } from '../layouts/AppLayout';
import { LoginPage } from '../features/auth/LoginPage';
import { DashboardPage } from '../features/dashboard/DashboardPage';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    path: '/',
    element: <AppLayout />,
    children: [{ index: true, element: <DashboardPage /> }],
  },
  { path: '*', element: <Navigate to="/" replace /> },
]);
```

```tsx
// D:\workspace\bookmark-hub\frontend\src\features\auth\LoginPage.tsx
import { Button, Card, Form, Input, Typography } from 'antd';

export function LoginPage() {
  return (
    <Card title="Bookmark Hub 登录">
      <Form layout="vertical">
        <Form.Item label="用户名" name="username">
          <Input />
        </Form.Item>
        <Form.Item label="密码" name="password">
          <Input.Password />
        </Form.Item>
        <Button type="primary" htmlType="submit">
          登录
        </Button>
      </Form>
      <Typography.Paragraph>团队书签集中管理平台</Typography.Paragraph>
    </Card>
  );
}
```

- [ ] **Step 5: Re-run the frontend test to confirm the first green state**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npx vitest run src/features/auth/LoginPage.test.tsx
```

Expected:

- PASS with 1 passed test

- [ ] **Step 6: Commit the frontend shell**

```powershell
git add frontend
git commit -m "feat: scaffold react frontend shell"
```

### Task 3: Scaffold The Backend And Base Configuration

**Files:**
- Create: `D:\workspace\bookmark-hub\backend\pom.xml`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\BookmarkHubApplication.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\resources\application.yml`
- Test: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\BookmarkHubApplicationTests.java`

- [ ] **Step 1: Generate the Spring Boot project with Maven Wrapper**

```powershell
Invoke-WebRequest `
  -Uri "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=3.4.5&baseDir=backend&groupId=com.bookmarkhub&artifactId=bookmark-hub&name=bookmark-hub&packageName=com.bookmarkhub&packaging=jar&javaVersion=21&dependencies=web,data-jpa,security,validation,lombok,h2,mysql,flyway" `
  -OutFile "D:\workspace\bookmark-hub\backend.zip"
Expand-Archive -Path "D:\workspace\bookmark-hub\backend.zip" -DestinationPath "D:\workspace\bookmark-hub" -Force
Remove-Item "D:\workspace\bookmark-hub\backend.zip"
```

- [ ] **Step 2: Add a failing context boot test for the backend**

```java
package com.bookmarkhub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookmarkHubApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 3: Run the backend tests to verify the base project fails until config is ready**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test
```

Expected:

- FAIL because datasource properties and Flyway migrations are not ready yet

- [ ] **Step 4: Add base application settings for local MySQL and test H2**

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/bookmark_hub?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
  flyway:
    enabled: true
server:
  port: 8080
app:
  jwt:
    secret: ${JWT_SECRET:bookmark-hub-dev-secret-bookmark-hub-dev-secret}
```

- [ ] **Step 5: Re-run the backend tests and verify the remaining failure is the missing schema**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=BookmarkHubApplicationTests
```

Expected:

- FAIL with missing database objects, which confirms the migration task is necessary

- [ ] **Step 6: Commit the backend scaffold**

```powershell
git add backend
git commit -m "feat: scaffold spring boot backend"
```

### Task 4: Implement Schema, Seed Data, And Authentication

**Files:**
- Create: `D:\workspace\bookmark-hub\backend\src\main\resources\db\migration\V1__init_schema.sql`
- Create: `D:\workspace\bookmark-hub\backend\src\main\resources\db\migration\V2__seed_admin.sql`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\auth\AuthController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\auth\AuthService.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\config\SecurityConfig.java`
- Test: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\auth\AuthControllerTest.java`

- [ ] **Step 1: Write a failing authentication test**

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginReturnsJwtForSeededAdmin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"admin","password":"Admin@123456"}
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.username").value("admin"));
    }
}
```

- [ ] **Step 2: Run the auth test to verify it fails before auth code exists**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=AuthControllerTest
```

Expected:

- FAIL with missing endpoint or missing tables

- [ ] **Step 3: Add the initial schema and seed data**

```sql
CREATE TABLE team (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

```sql
INSERT INTO team (id, name, description) VALUES (1, '默认团队', 'Bookmark Hub 默认团队');
INSERT INTO users (id, username, password_hash, display_name, email, status)
VALUES (1, 'admin', '$2a$10$7m0x3xK6L2UN7R9VUDh61uR/VM5k4G4e90Qx0S1U9O3QWS9dC1B6a', '系统管理员', 'admin@bookmarkhub.local', 'ACTIVE');
INSERT INTO team_member (id, team_id, user_id, role, joined_at)
VALUES (1, 1, 1, 'ADMIN', CURRENT_TIMESTAMP);
```

- [ ] **Step 4: Add minimal JWT login and current-user endpoints**

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse me(Authentication authentication) {
        return authService.currentUser(authentication.getName());
    }
}
```

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

- [ ] **Step 5: Re-run the authentication test and verify it passes**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=AuthControllerTest
```

Expected:

- PASS with token and seeded admin user

- [ ] **Step 6: Commit schema and authentication**

```powershell
git add backend
git commit -m "feat: add schema migrations and authentication"
```

### Task 5: Implement Category, Member, Bookmark, And Dashboard APIs

**Files:**
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\category\CategoryController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\member\TeamMemberController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\bookmark\BookmarkController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\dashboard\DashboardController.java`
- Test: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\category\CategoryControllerTest.java`
- Test: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\bookmark\BookmarkControllerTest.java`

- [ ] **Step 1: Write a failing category permission test**

```java
@Test
void memberCannotCreateCategory() throws Exception {
    mockMvc.perform(post("/api/categories")
            .header(HttpHeaders.AUTHORIZATION, bearerTokenFor("member1"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"name":"前端","parentId":null}
                    """))
        .andExpect(status().isForbidden());
}
```

- [ ] **Step 2: Write a failing bookmark ownership test**

```java
@Test
void memberCannotUpdateAnotherMembersBookmark() throws Exception {
    mockMvc.perform(put("/api/bookmarks/2")
            .header(HttpHeaders.AUTHORIZATION, bearerTokenFor("member2"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title":"改名","url":"https://example.com","description":"x","categoryId":1}
                    """))
        .andExpect(status().isForbidden());
}
```

- [ ] **Step 3: Run the category and bookmark tests to verify the red state**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=CategoryControllerTest,BookmarkControllerTest
```

Expected:

- FAIL because the controllers and permission checks do not exist yet

- [ ] **Step 4: Implement the domain models, repositories, services, and controllers**

```java
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping
    public PageResponse<BookmarkSummaryResponse> list(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long categoryId,
                                                      Authentication authentication) {
        return bookmarkService.list(authentication.getName(), keyword, categoryId);
    }

    @PostMapping
    public BookmarkDetailResponse create(@Valid @RequestBody SaveBookmarkRequest request,
                                         Authentication authentication) {
        return bookmarkService.create(authentication.getName(), request);
    }
}
```

```java
if (!actor.isAdmin() && !bookmark.getCreatedBy().equals(actor.userId())) {
    throw new AccessDeniedException("Only owner or admin can modify bookmark");
}
```

```java
@GetMapping("/overview")
public DashboardOverviewResponse overview(Authentication authentication) {
    return dashboardService.getOverview(authentication.getName());
}
```

- [ ] **Step 5: Re-run the backend API tests and verify the green state**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=CategoryControllerTest,BookmarkControllerTest
```

Expected:

- PASS with category admin-only enforcement and bookmark ownership enforcement

- [ ] **Step 6: Commit the core management APIs**

```powershell
git add backend
git commit -m "feat: add category member bookmark and dashboard APIs"
```

### Task 6: Implement Bookmark HTML Parsing And Import Confirmation

**Files:**
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\importing\ImportController.java`
- Create: `D:\workspace\bookmark-hub\backend\src\main\java\com\bookmarkhub\importing\ImportService.java`
- Create: `D:\workspace\bookmark-hub\backend\src\test\java\com\bookmarkhub\importing\ImportControllerTest.java`
- Modify: `D:\workspace\bookmark-hub\backend\pom.xml`

- [ ] **Step 1: Add a failing import parse test**

```java
@Test
void parseHtmlReturnsPreviewRows() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "bookmarks.html",
        "text/html",
        """
        <!DOCTYPE NETSCAPE-Bookmark-file-1>
        <DL><p>
          <DT><H3>开发</H3>
          <DL><p>
            <DT><A HREF="https://react.dev">React</A>
          </DL><p>
        </DL><p>
        """.getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/api/imports/parse")
            .file(file)
            .header(HttpHeaders.AUTHORIZATION, bearerTokenFor("admin")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].title").value("React"))
        .andExpect(jsonPath("$.items[0].folderPath").value("开发"));
}
```

- [ ] **Step 2: Run the import test to verify it fails before parser code exists**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=ImportControllerTest
```

Expected:

- FAIL with missing multipart endpoint or parser logic

- [ ] **Step 3: Add Jsoup and implement the parse and confirm endpoints**

```xml
<dependency>
  <groupId>org.jsoup</groupId>
  <artifactId>jsoup</artifactId>
  <version>1.18.1</version>
</dependency>
```

```java
@PostMapping(path = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ImportPreviewResponse parse(@RequestPart("file") MultipartFile file,
                                   Authentication authentication) {
    return importService.parse(authentication.getName(), file);
}

@PostMapping("/confirm")
public ImportResultResponse confirm(@Valid @RequestBody ConfirmImportRequest request,
                                    Authentication authentication) {
    return importService.confirm(authentication.getName(), request);
}
```

- [ ] **Step 4: Re-run the import tests and verify preview plus confirmation pass**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test -Dtest=ImportControllerTest
```

Expected:

- PASS with parsed preview rows and persisted import record assertions

- [ ] **Step 5: Commit the import feature**

```powershell
git add backend
git commit -m "feat: add bookmark html import flow"
```

### Task 7: Implement Frontend Authentication, Layout, And Dashboard

**Files:**
- Create: `D:\workspace\bookmark-hub\frontend\src\api\http.ts`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\auth\authStore.ts`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\dashboard\DashboardPage.tsx`
- Modify: `D:\workspace\bookmark-hub\frontend\src\features\auth\LoginPage.tsx`
- Test: `D:\workspace\bookmark-hub\frontend\src\features\auth\LoginPage.test.tsx`

- [ ] **Step 1: Extend the login page test to assert submit behavior**

```tsx
test('submits credentials and stores token on successful login', async () => {
  server.use(
    http.post('/api/auth/login', async () =>
      HttpResponse.json({ token: 'jwt-token', username: 'admin', role: 'ADMIN' }),
    ),
  );

  render(<App />);
  await userEvent.type(screen.getByLabelText('用户名'), 'admin');
  await userEvent.type(screen.getByLabelText('密码'), 'Admin@123456');
  await userEvent.click(screen.getByRole('button', { name: '登录' }));

  await waitFor(() => expect(localStorage.getItem('bookmark-hub-token')).toBe('jwt-token'));
});
```

- [ ] **Step 2: Run the login page test to verify the red state**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npx vitest run src/features/auth/LoginPage.test.tsx
```

Expected:

- FAIL because the API layer and auth store are not wired yet

- [ ] **Step 3: Add the API client, auth store, route guard, and dashboard cards**

```ts
export const httpClient = axios.create({
  baseURL: 'http://localhost:8080/api',
});

httpClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('bookmark-hub-token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

```ts
type AuthState = {
  token: string | null;
  setSession: (token: string) => void;
  clearSession: () => void;
};
```

```tsx
<Row gutter={16}>
  <Col span={8}><Card title="书签总数">{overview.bookmarkCount}</Card></Col>
  <Col span={8}><Card title="分类总数">{overview.categoryCount}</Card></Col>
  <Col span={8}><Card title="成员总数">{overview.memberCount}</Card></Col>
</Row>
```

- [ ] **Step 4: Re-run the login and dashboard tests to verify green**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npx vitest run src/features/auth/LoginPage.test.tsx
```

Expected:

- PASS with persisted token and redirected dashboard render

- [ ] **Step 5: Commit the frontend auth shell**

```powershell
git add frontend
git commit -m "feat: add frontend auth flow and dashboard shell"
```

### Task 8: Implement Frontend CRUD Pages For Bookmarks, Categories, Members, And Imports

**Files:**
- Create: `D:\workspace\bookmark-hub\frontend\src\features\bookmarks\BookmarkPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\categories\CategoryPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\members\MemberPage.tsx`
- Create: `D:\workspace\bookmark-hub\frontend\src\features\imports\ImportPage.tsx`
- Test: `D:\workspace\bookmark-hub\frontend\src\features\bookmarks\BookmarkPage.test.tsx`
- Test: `D:\workspace\bookmark-hub\frontend\src\features\imports\ImportPage.test.tsx`

- [ ] **Step 1: Write a failing bookmark page test**

```tsx
test('shows bookmark rows from the API', async () => {
  server.use(
    http.get('/api/bookmarks', async () =>
      HttpResponse.json({ items: [{ id: 1, title: 'React', url: 'https://react.dev', creatorName: 'admin' }] }),
    ),
  );

  render(<BookmarkPage />);
  expect(await screen.findByText('React')).toBeInTheDocument();
  expect(screen.getByText('https://react.dev')).toBeInTheDocument();
});
```

- [ ] **Step 2: Write a failing import page test**

```tsx
test('renders parsed import preview rows after upload', async () => {
  server.use(
    http.post('/api/imports/parse', async () =>
      HttpResponse.json({ items: [{ title: 'React', url: 'https://react.dev', folderPath: '开发' }] }),
    ),
  );

  render(<ImportPage />);
  const file = new File(['dummy'], 'bookmarks.html', { type: 'text/html' });
  await userEvent.upload(screen.getByLabelText('上传书签文件'), file);
  expect(await screen.findByText('React')).toBeInTheDocument();
});
```

- [ ] **Step 3: Run the page tests to verify both are red**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npx vitest run src/features/bookmarks/BookmarkPage.test.tsx src/features/imports/ImportPage.test.tsx
```

Expected:

- FAIL because the pages and data hooks are not implemented yet

- [ ] **Step 4: Implement the pages and hooks with the minimal UI needed for the approved scope**

```tsx
<Table
  rowKey="id"
  dataSource={data?.items ?? []}
  columns={[
    { title: '标题', dataIndex: 'title' },
    { title: 'URL', dataIndex: 'url' },
    { title: '创建人', dataIndex: 'creatorName' },
  ]}
/>
```

```tsx
<Upload beforeUpload={(file) => { void parseFile(file); return false; }}>
  <Button icon={<UploadOutlined />}>上传书签文件</Button>
</Upload>
```

```tsx
<Tree treeData={treeData} />
<Form layout="vertical">
  <Form.Item label="分类名称" name="name"><Input /></Form.Item>
</Form>
```

- [ ] **Step 5: Re-run the page tests and verify the green state**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npx vitest run src/features/bookmarks/BookmarkPage.test.tsx src/features/imports/ImportPage.test.tsx
```

Expected:

- PASS with bookmark rows and import preview rows rendered

- [ ] **Step 6: Commit the frontend management pages**

```powershell
git add frontend
git commit -m "feat: add frontend management pages"
```

### Task 9: Verify End-To-End Behavior And Finish Documentation

**Files:**
- Modify: `D:\workspace\bookmark-hub\README.md`
- Modify: `D:\workspace\bookmark-hub\backend\src\main\resources\application.yml`
- Modify: `D:\workspace\bookmark-hub\frontend\src\router\index.tsx`

- [ ] **Step 1: Run the complete automated test suite**

Run:

```powershell
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd test
cd D:\workspace\bookmark-hub\frontend
npx vitest run
```

Expected:

- All backend tests PASS
- All frontend tests PASS

- [ ] **Step 2: Start MySQL, create the database, and verify the backend boots against it**

Run:

```powershell
mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS bookmark_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
cd D:\workspace\bookmark-hub\backend
.\mvnw.cmd spring-boot:run
```

Expected:

- Spring Boot starts on port `8080`
- Flyway applies `V1__init_schema.sql` and `V2__seed_admin.sql`

- [ ] **Step 3: Start the frontend and verify the happy path manually**

Run:

```powershell
cd D:\workspace\bookmark-hub\frontend
npm run dev
```

Manual checklist:

- Open `http://localhost:5173`
- Login with `admin / Admin@123456`
- Create a member
- Create a category
- Create a bookmark
- Confirm a non-owner cannot edit another member's bookmark
- Upload a sample bookmarks HTML file and confirm preview plus import

- [ ] **Step 4: Update the README with verified startup and demo steps**

```markdown
## Verified Local Flow

1. Start MySQL and create `bookmark_hub`
2. Run `backend\mvnw.cmd spring-boot:run`
3. Run `frontend\npm run dev`
4. Open `http://localhost:5173`
5. Login with `admin / Admin@123456`
```

- [ ] **Step 5: Commit the final verified MVP**

```powershell
git add .
git commit -m "feat: deliver bookmark hub mvp"
```

## Self-Review Checklist

- Spec coverage confirmed for login, permissions, dashboard, category management, member management, bookmark CRUD, HTML import, seeded admin account, and local startup documentation.
- Placeholder scan complete. No placeholder text remains.
- Naming consistency checked for `BookmarkPage`, `ImportPage`, `AuthController`, `BookmarkController`, `CategoryController`, `ImportController`, `team_member`, and `users`.
