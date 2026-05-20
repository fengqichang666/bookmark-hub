# Task 2 Red-Green Evidence

## Scope

Evidence for the Task 2 TDD checkpoint on `frontend/src/features/auth/LoginPage.test.tsx`.

## Red

Method:

- Temporarily removed the `label` props from the username and password `Form.Item` elements in `frontend/src/features/auth/LoginPage.tsx`
- Ran `npx vitest run src/features/auth/LoginPage.test.tsx`

Observed failure:

```text
FAIL  src/features/auth/LoginPage.test.tsx > LoginPage > renders username and password inputs
TestingLibraryElementError: Unable to find a label with the text of: 用户名
```

This is the expected red-state failure for missing accessible form labels. The test is failing on the intended behavior rather than on an import/setup problem.

## Green

Method:

- Restored the `label="用户名"` and `label="密码"` props in `frontend/src/features/auth/LoginPage.tsx`
- Re-ran `npx vitest run src/features/auth/LoginPage.test.tsx`

Observed result:

```text
Test Files  1 passed (1)
Tests       1 passed (1)
```

This confirms the same behavior-driven test passes after the real labels are restored, and the final workspace keeps the correct implementation.
