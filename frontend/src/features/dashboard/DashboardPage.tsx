import { Card, Typography } from 'antd'

function DashboardPage() {
  return (
    <Card className="dashboard-placeholder">
      <Typography.Title level={3}>Dashboard</Typography.Title>
      <Typography.Paragraph type="secondary">
        Bookmark Hub 前端骨架已就绪，后续功能将在这里继续扩展。
      </Typography.Paragraph>
    </Card>
  )
}

export default DashboardPage
