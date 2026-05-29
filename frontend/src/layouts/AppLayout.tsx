import {
  BookOutlined,
  FolderOpenOutlined,
  HomeOutlined,
  ImportOutlined,
  LogoutOutlined,
  TeamOutlined,
} from '@ant-design/icons'
import { Button, Layout, Menu, Typography } from 'antd'
import { useMemo, useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../features/auth/authStore'

const { Sider, Content } = Layout

const menuItems = [
  { key: '/', icon: <HomeOutlined />, label: '首页' },
  { key: '/bookmarks', icon: <BookOutlined />, label: '书签' },
  { key: '/categories', icon: <FolderOpenOutlined />, label: '分类' },
  { key: '/members', icon: <TeamOutlined />, label: '成员' },
  { key: '/imports', icon: <ImportOutlined />, label: '导入' },
]

function AppLayout() {
  const location = useLocation()
  const navigate = useNavigate()
  const [collapsed, setCollapsed] = useState(false)
  const clearSession = useAuthStore((state) => state.clearSession)

  const selectedKey = useMemo(() => {
    const matchedItem = [...menuItems]
      .sort((left, right) => right.key.length - left.key.length)
      .find((item) =>
        item.key === '/'
          ? location.pathname === '/'
          : location.pathname === item.key || location.pathname.startsWith(`${item.key}/`),
      )

    return matchedItem?.key ?? '/'
  }, [location.pathname])

  return (
    <Layout className="app-shell">
      <Sider
        theme="light"
        width={224}
        breakpoint="lg"
        collapsedWidth={80}
        collapsed={collapsed}
        onBreakpoint={setCollapsed}
        className="app-shell__sider"
      >
        <div className="app-shell__brand">
          <Typography.Title level={4} className="app-shell__brand-title">
            Bookmark Hub
          </Typography.Title>
        </div>
        <Menu
          mode="inline"
          theme="light"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => {
            navigate(key)
          }}
          className="app-shell__menu"
        />
        <div className="app-shell__logout">
          <Button
            type="text"
            icon={<LogoutOutlined />}
            aria-label="退出登录"
            className="app-shell__logout-button"
            onClick={() => {
              clearSession()
              navigate('/login', { replace: true })
            }}
          >
            {!collapsed ? '退出登录' : null}
          </Button>
        </div>
      </Sider>
      <Layout className="app-shell__main">
        <Content className="app-shell__content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default AppLayout
