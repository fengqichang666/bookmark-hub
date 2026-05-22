import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/server'
import ImportPage from './ImportPage'

function renderImportPage() {
  const queryClient = new QueryClient()
  render(
    <QueryClientProvider client={queryClient}>
      <ImportPage />
    </QueryClientProvider>,
  )
}

describe('ImportPage', () => {
  it('renders parsed import preview rows after upload', async () => {
    server.use(
      http.get(/\/api\/categories$/, async () =>
        HttpResponse.json({
          items: [{ id: 1, name: 'Imported', parentId: null }],
        }),
      ),
      http.post(/\/api\/imports\/parse$/, async () =>
        HttpResponse.json({
          fileName: 'bookmarks.html',
          items: [{ title: 'React', url: 'https://react.dev', folderPath: '开发' }],
        }),
      ),
    )

    renderImportPage()

    const file = new File(['dummy'], 'bookmarks.html', { type: 'text/html' })
    await userEvent.upload(await screen.findByLabelText('上传书签文件'), file)

    expect(await screen.findByText('React')).toBeInTheDocument()
  })
})
