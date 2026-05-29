import { render, screen, within } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import AppLayout from './AppLayout'

describe('AppLayout', () => {
  it('renders the sidebar menu and highlights the current route for signed-in users', () => {
    render(
      <MemoryRouter initialEntries={['/bookmarks']}>
        <Routes>
          <Route path="/" element={<AppLayout />}>
            <Route index element={<div>首页内容</div>} />
            <Route path="bookmarks" element={<div>书签内容</div>} />
            <Route path="categories" element={<div>分类内容</div>} />
            <Route path="members" element={<div>成员内容</div>} />
            <Route path="imports" element={<div>导入内容</div>} />
          </Route>
        </Routes>
      </MemoryRouter>,
    )

    const navigation = screen.getByRole('menu')
    expect(within(navigation).getByText('首页')).toBeInTheDocument()
    expect(within(navigation).getByText('书签')).toBeInTheDocument()

    const activeItem = within(navigation).getByText('书签').closest('.ant-menu-item-selected')
    expect(activeItem).not.toBeNull()
    expect(screen.getByText('书签内容')).toBeInTheDocument()
  })
})
