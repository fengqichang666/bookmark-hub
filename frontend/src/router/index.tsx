import { createBrowserRouter } from 'react-router-dom'
import AppLayout from '../layouts/AppLayout'
import LoginPage from '../features/auth/LoginPage'
import DashboardPage from '../features/dashboard/DashboardPage'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'login',
        element: <LoginPage />,
      },
    ],
  },
])
