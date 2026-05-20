# Bookmark Hub

## Local Development

The frontend scaffold for Task 2 now exists under `frontend/`.

The backend is not scaffolded yet, so backend startup commands are not available at this stage.

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
