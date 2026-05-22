import { Layout, Space, Typography } from 'antd'
import { Link, Outlet } from 'react-router-dom'

const { Header, Content } = Layout

function AppLayout() {
  return (
    <Layout className="app-shell">
      <Header className="app-shell__header">
        <Typography.Title level={4} className="app-shell__brand">
          Bookmark Hub
        </Typography.Title>
        <Space size="large">
          <Link to="/">首页</Link>
          <Link to="/bookmarks">书签</Link>
          <Link to="/categories">分类</Link>
          <Link to="/members">成员</Link>
          <Link to="/imports">导入</Link>
        </Space>
      </Header>
      <Content className="app-shell__content">
        <Outlet />
      </Content>
    </Layout>
  )
}

export default AppLayout
