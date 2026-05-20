# Bookmark Hub

## Local Development

The frontend scaffold for Task 2 now exists under `frontend/`.

The backend scaffold for Task 3 now exists under `backend/`.

The backend is not fully runnable yet because schema migrations and database objects have not been added. The current backend context boot test is expected to fail at schema validation until those migrations arrive in a later task.

### Frontend

Install dependencies:

```bash
cd frontend
npm install --no-audit
```

Start the Vite dev server:

```bash
cd frontend
npm run dev
```

Run the current frontend checks:

```bash
cd frontend
npx vitest run src/features/auth/LoginPage.test.tsx
npm run build
npm run lint
```

### Backend

Run the current backend context boot check:

```bash
cd backend
./mvnw.cmd test -Dtest=BookmarkHubApplicationTests
```

Current expected result:

- The Spring Boot app starts far enough to validate JPA mappings
- The test fails because the schema is still missing required database tables
