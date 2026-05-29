import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/server'
import BookmarkPage from './BookmarkPage'

const categoryItems = [
  { id: 1, name: 'Frontend', parentId: null },
  { id: 2, name: 'Backend', parentId: null },
]

type BookmarkListItem = {
  id: number
  title: string
  url: string
  creatorName: string
}

function renderBookmarkPage() {
  const queryClient = new QueryClient()
  render(
    <QueryClientProvider client={queryClient}>
      <BookmarkPage />
    </QueryClientProvider>,
  )
}

describe('BookmarkPage', () => {
  it('opens the create modal and submits a new bookmark', async () => {
    let createPayload: Record<string, unknown> | undefined
    let bookmarkItems: BookmarkListItem[] = []

    server.use(
      http.get(/\/api\/bookmarks$/, async () =>
        HttpResponse.json({
          items: bookmarkItems,
        }),
      ),
      http.get(/\/api\/categories$/, async () =>
        HttpResponse.json({
          items: categoryItems,
        }),
      ),
      http.post(/\/api\/bookmarks$/, async ({ request }) => {
        createPayload = (await request.json()) as Record<string, unknown>
        bookmarkItems = [
          {
            id: 2,
            title: 'Vue',
            url: 'https://vuejs.org',
            creatorName: 'admin',
          },
        ]
        return HttpResponse.json({ id: 2 }, { status: 201 })
      }),
    )

    renderBookmarkPage()

    await userEvent.click(await screen.findByRole('button', { name: '新增书签' }))

    expect(await screen.findByRole('dialog', { name: '新增书签' })).toBeInTheDocument()

    await userEvent.type(screen.getByLabelText('标题'), 'Vue')
    await userEvent.type(screen.getByLabelText('URL'), 'https://vuejs.org')
    await userEvent.type(screen.getByLabelText('描述'), 'Progressive framework')
    await userEvent.click(screen.getByLabelText('分类'))
    await userEvent.click(await screen.findByText('Frontend'))
    await userEvent.click(screen.getByRole('button', { name: /保\s*存/ }))

    expect(await screen.findByText('Vue')).toBeInTheDocument()
    expect(createPayload).toEqual({
      title: 'Vue',
      url: 'https://vuejs.org',
      description: 'Progressive framework',
      categoryId: 1,
    })
  })

  it('opens the edit modal, loads details, and submits updates', async () => {
    let updatePayload: Record<string, unknown> | undefined
    let bookmarkItems: BookmarkListItem[] = [
      {
        id: 1,
        title: 'React',
        url: 'https://react.dev',
        creatorName: 'admin',
      },
    ]

    server.use(
      http.get(/\/api\/bookmarks$/, async () =>
        HttpResponse.json({
          items: bookmarkItems,
        }),
      ),
      http.get(/\/api\/categories$/, async () =>
        HttpResponse.json({
          items: categoryItems,
        }),
      ),
      http.get(/\/api\/bookmarks\/1$/, async () =>
        HttpResponse.json({
          id: 1,
          title: 'React',
          url: 'https://react.dev',
          description: 'UI library',
          categoryId: 1,
          creatorName: 'admin',
        }),
      ),
      http.put(/\/api\/bookmarks\/1$/, async ({ request }) => {
        updatePayload = (await request.json()) as Record<string, unknown>
        bookmarkItems = [
          {
            id: 1,
            title: 'React Router',
            url: 'https://reactrouter.com',
            creatorName: 'admin',
          },
        ]
        return HttpResponse.json({ id: 1 })
      }),
    )

    renderBookmarkPage()

    await userEvent.click(await screen.findByRole('button', { name: '编辑' }))

    expect(await screen.findByDisplayValue('React')).toBeInTheDocument()
    expect(screen.getByDisplayValue('https://react.dev')).toBeInTheDocument()
    expect(screen.getByDisplayValue('UI library')).toBeInTheDocument()

    await userEvent.clear(screen.getByLabelText('标题'))
    await userEvent.type(screen.getByLabelText('标题'), 'React Router')
    await userEvent.clear(screen.getByLabelText('URL'))
    await userEvent.type(screen.getByLabelText('URL'), 'https://reactrouter.com')
    await userEvent.clear(screen.getByLabelText('描述'))
    await userEvent.type(screen.getByLabelText('描述'), 'Routing library')
    await userEvent.click(screen.getByLabelText('分类'))
    await userEvent.click(await screen.findByText('Backend'))
    await userEvent.click(screen.getByRole('button', { name: /保\s*存/ }))

    expect(await screen.findByText('React Router')).toBeInTheDocument()
    expect(updatePayload).toEqual({
      title: 'React Router',
      url: 'https://reactrouter.com',
      description: 'Routing library',
      categoryId: 2,
    })
  })

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
