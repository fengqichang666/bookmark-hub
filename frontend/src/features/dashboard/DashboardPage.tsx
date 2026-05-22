import { useQuery } from '@tanstack/react-query'
import { Card, Col, Row, Skeleton, Typography } from 'antd'
import { httpClient } from '../../api/http'

type DashboardOverview = {
  bookmarkCount: number
  categoryCount: number
  memberCount: number
}

function DashboardPage() {
  const { data, isPending } = useQuery({
    queryKey: ['dashboard-overview'],
    queryFn: async () => {
      const response = await httpClient.get<DashboardOverview>('/dashboard/overview')
      return response.data
    },
  })

  if (isPending) {
    return (
      <Card className="dashboard-placeholder">
        <Skeleton active paragraph={{ rows: 1 }} />
      </Card>
    )
  }

  return (
    <div className="dashboard-overview">
      <Typography.Title level={3}>团队概览</Typography.Title>
      <Typography.Paragraph type="secondary">
        这里展示团队书签、分类和成员的当前统计。
      </Typography.Paragraph>
      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <Card title="书签总数">{data?.bookmarkCount ?? 0}</Card>
        </Col>
        <Col xs={24} md={8}>
          <Card title="分类总数">{data?.categoryCount ?? 0}</Card>
        </Col>
        <Col xs={24} md={8}>
          <Card title="成员总数">{data?.memberCount ?? 0}</Card>
        </Col>
      </Row>
    </div>
  )
}

export default DashboardPage
