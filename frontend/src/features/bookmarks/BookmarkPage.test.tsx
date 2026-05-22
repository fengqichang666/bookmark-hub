import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/server'
import BookmarkPage from './BookmarkPage'

function renderBookmarkPage() {
  const queryClient = new QueryClient()
  render(
    <QueryClientProvider client={queryClient}>
      <BookmarkPage />
    </QueryClientProvider>,
  )
}

describe('BookmarkPage', () => {
  it('shows bookmark rows from the API', async () => {
    server.use(
      http.get(/\/api\/bookmarks$/, async () =>
        HttpResponse.json({
          items: [
            {
              id: 1,
              title: 'React',
              url: 'https://react.dev',
              creatorName: 'admin',
            },
          ],
        }),
      ),
    )

    renderBookmarkPage()

    expect(await screen.findByText('React')).toBeInTheDocument()
    expect(screen.getByText('https://react.dev')).toBeInTheDocument()
  })
})
