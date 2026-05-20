# Task 2 Red-Green Evidence

## Scope

Evidence for the Task 2 TDD checkpoint on `frontend/src/features/auth/LoginPage.test.tsx`.

## Red

Method:

- Temporarily moved `frontend/src/features/auth/LoginPage.tsx` aside
- Ran `npx vitest run src/features/auth/LoginPage.test.tsx`

Observed failure:

```text
FAIL  src/features/auth/LoginPage.test.tsx [ src/features/auth/LoginPage.test.tsx ]
Error: Failed to resolve import "./LoginPage" from "src/features/auth/LoginPage.test.tsx". Does the file exist?
```

This is the expected red-state failure for the missing implementation file.

## Green

Method:

- Restored `frontend/src/features/auth/LoginPage.tsx`
- Re-ran `npx vitest run src/features/auth/LoginPage.test.tsx`

Observed result:

```text
Test Files  1 passed (1)
Tests       1 passed (1)
```

This confirms the same test passes after the implementation is present.
