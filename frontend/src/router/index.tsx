import type { ReactNode } from 'react'
import { Navigate, createBrowserRouter } from 'react-router-dom'
import AppLayout from '../layouts/AppLayout'
import LoginPage from '../features/auth/LoginPage'
import { useAuthStore } from '../features/auth/authStore'
import DashboardPage from '../features/dashboard/DashboardPage'
import BookmarkPage from '../features/bookmarks/BookmarkPage'
import CategoryPage from '../features/categories/CategoryPage'
import MemberPage from '../features/members/MemberPage'
import ImportPage from '../features/imports/ImportPage'

function RequireAuth({ children }: { children: ReactNode }) {
  const token = useAuthStore((state) => state.token)
  return token ? children : <Navigate to="/login" replace />
}

function GuestOnly({ children }: { children: ReactNode }) {
  const token = useAuthStore((state) => state.token)
  return token ? <Navigate to="/" replace /> : children
}

export const router = createBrowserRouter([
  {
    path: '/login',
    element: (
      <GuestOnly>
        <LoginPage />
      </GuestOnly>
    ),
  },
  {
    path: '/',
    element: (
      <RequireAuth>
        <AppLayout />
      </RequireAuth>
    ),
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'bookmarks', element: <BookmarkPage /> },
      { path: 'categories', element: <CategoryPage /> },
      { path: 'members', element: <MemberPage /> },
      { path: 'imports', element: <ImportPage /> },
    ],
  },
  { path: '*', element: <Navigate to="/" replace /> },
])
