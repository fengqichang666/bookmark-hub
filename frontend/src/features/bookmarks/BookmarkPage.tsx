import { useQuery } from '@tanstack/react-query'
import { Card, Table, Typography } from 'antd'
import { httpClient, type PageResponse } from '../../api/http'

type BookmarkRow = {
  id: number
  title: string
  url: string
  creatorName: string
}

function BookmarkPage() {
  const { data, isPending } = useQuery({
    queryKey: ['bookmarks'],
    queryFn: async () => {
      const response = await httpClient.get<PageResponse<BookmarkRow>>('/bookmarks')
      return response.data.items
    },
  })

  return (
    <Card>
      <Typography.Title level={3}>书签管理</Typography.Title>
      <Typography.Paragraph type="secondary">
        这里展示当前团队可见的书签列表。
      </Typography.Paragraph>
      <Table<BookmarkRow>
        rowKey="id"
        loading={isPending}
        pagination={false}
        dataSource={data ?? []}
        columns={[
          { title: '标题', dataIndex: 'title' },
          { title: 'URL', dataIndex: 'url' },
          { title: '创建人', dataIndex: 'creatorName' },
        ]}
      />
    </Card>
  )
}

export default BookmarkPage
