import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { MemoryRouter } from 'react-router-dom'
import App from '../../App'
import { server } from '../../test/server'
import LoginPage from './LoginPage'

describe('LoginPage', () => {
  it('renders username and password inputs', () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )

    expect(screen.getByLabelText('用户名')).toBeInTheDocument()
    expect(screen.getByLabelText('密码')).toBeInTheDocument()
  })

  it('submits credentials and stores token on successful login', async () => {
    server.use(
      http.post(/\/api\/auth\/login$/, async () =>
        HttpResponse.json({
          token: 'jwt-token',
          user: {
            username: 'admin',
            displayName: 'System Administrator',
            role: 'ADMIN',
            teamId: 1,
          },
        }),
      ),
      http.get(/\/api\/dashboard\/overview$/, async () =>
        HttpResponse.json({
          bookmarkCount: 12,
          categoryCount: 3,
          memberCount: 2,
        }),
      ),
    )

    const user = userEvent.setup()

    render(<App />)

    await user.type(await screen.findByLabelText('用户名'), 'admin')
    await user.type(screen.getByLabelText('密码'), 'Admin@123456')
    await user.click(screen.getByRole('button', { name: /登\s*录/ }))

    await waitFor(() => {
      expect(window.localStorage.getItem('bookmark-hub-token')).toBe('jwt-token')
    })

    expect(await screen.findByText('书签总数')).toBeInTheDocument()
    expect(screen.getByText('12')).toBeInTheDocument()
  })
})
