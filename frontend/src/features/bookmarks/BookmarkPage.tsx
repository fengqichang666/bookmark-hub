import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Card, Form, Input, Modal, Select, Space, Table, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { httpClient, type PageResponse } from '../../api/http'

type BookmarkRow = {
  id: number
  title: string
  url: string
  creatorName: string
}

type CategoryItem = {
  id: number
  name: string
  parentId: number | null
}

type BookmarkDetail = {
  id: number
  title: string
  url: string
  description: string
  categoryId: number | null
  creatorName: string
}

type BookmarkFormValues = {
  title: string
  url: string
  description: string
  categoryId?: number
}

function BookmarkPage() {
  const queryClient = useQueryClient()
  const [form] = Form.useForm<BookmarkFormValues>()
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingBookmarkId, setEditingBookmarkId] = useState<number | null>(null)

  const { data, isPending } = useQuery({
    queryKey: ['bookmarks'],
    queryFn: async () => {
      const response = await httpClient.get<PageResponse<BookmarkRow>>('/bookmarks')
      return response.data.items
    },
  })

  const { data: categoryItems = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: async () => {
      const response = await httpClient.get<PageResponse<CategoryItem>>('/categories')
      return response.data.items
    },
    enabled: isModalOpen,
  })

  const { data: bookmarkDetail, isFetching: isFetchingDetail } = useQuery({
    queryKey: ['bookmark-detail', editingBookmarkId],
    queryFn: async () => {
      const response = await httpClient.get<BookmarkDetail>(`/bookmarks/${editingBookmarkId}`)
      return response.data
    },
    enabled: isModalOpen && editingBookmarkId != null,
  })

  useEffect(() => {
    if (!isModalOpen) {
      return
    }

    if (editingBookmarkId == null) {
      form.resetFields()
      return
    }

    if (bookmarkDetail) {
      form.setFieldsValue({
        title: bookmarkDetail.title,
        url: bookmarkDetail.url,
        description: bookmarkDetail.description,
        categoryId: bookmarkDetail.categoryId ?? undefined,
      })
    }
  }, [bookmarkDetail, editingBookmarkId, form, isModalOpen])

  const saveBookmark = useMutation({
    mutationFn: async (values: BookmarkFormValues) => {
      const payload = {
        title: values.title,
        url: values.url,
        description: values.description,
        categoryId: values.categoryId ?? null,
      }

      if (editingBookmarkId == null) {
        await httpClient.post('/bookmarks', payload)
        return
      }

      await httpClient.put(`/bookmarks/${editingBookmarkId}`, payload)
    },
    onSuccess: async () => {
      form.resetFields()
      setIsModalOpen(false)
      setEditingBookmarkId(null)
      await queryClient.invalidateQueries({ queryKey: ['bookmarks'] })
    },
  })

  function openCreateModal() {
    setEditingBookmarkId(null)
    form.resetFields()
    setIsModalOpen(true)
  }

  function openEditModal(id: number) {
    setEditingBookmarkId(id)
    form.resetFields()
    setIsModalOpen(true)
  }

  function closeModal() {
    if (saveBookmark.isPending) {
      return
    }

    setIsModalOpen(false)
    setEditingBookmarkId(null)
    form.resetFields()
  }

  return (
    <Card>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 8 }} align="start">
        <div>
          <Typography.Title level={3}>书签管理</Typography.Title>
          <Typography.Paragraph type="secondary">
            这里展示当前团队可见的书签列表。
          </Typography.Paragraph>
        </div>
        <Button type="primary" onClick={openCreateModal}>
          新增书签
        </Button>
      </Space>
      <Table<BookmarkRow>
        rowKey="id"
        loading={isPending}
        pagination={false}
        dataSource={data ?? []}
        columns={[
          { title: '标题', dataIndex: 'title' },
          { title: 'URL', dataIndex: 'url' },
          { title: '创建人', dataIndex: 'creatorName' },
          {
            title: '操作',
            key: 'actions',
            render: (_, record) => (
              <Button type="link" onClick={() => openEditModal(record.id)}>
                编辑
              </Button>
            ),
          },
        ]}
      />
      <Modal
        title={editingBookmarkId == null ? '新增书签' : '编辑书签'}
        open={isModalOpen}
        onCancel={closeModal}
        onOk={() => form.submit()}
        okText="保存"
        destroyOnHidden
        transitionName=""
        maskTransitionName=""
        confirmLoading={saveBookmark.isPending}
        cancelButtonProps={{ disabled: saveBookmark.isPending }}
      >
        <Form<BookmarkFormValues>
          form={form}
          layout="vertical"
          onFinish={(values) => saveBookmark.mutate(values)}
          disabled={isFetchingDetail}
        >
          <Form.Item label="标题" name="title" rules={[{ required: true, message: '请输入标题' }]}>
            <Input placeholder="例如：React" />
          </Form.Item>
          <Form.Item
            label="URL"
            name="url"
            rules={[
              { required: true, message: '请输入 URL' },
              { type: 'url', message: '请输入合法的 URL' },
            ]}
          >
            <Input placeholder="https://example.com" />
          </Form.Item>
          <Form.Item label="描述" name="description">
            <Input.TextArea placeholder="可选" rows={4} />
          </Form.Item>
          <Form.Item label="分类" name="categoryId" rules={[{ required: true, message: '请选择分类' }]}>
            <Select
              allowClear
              placeholder="请选择分类"
              options={categoryItems.map((category) => ({
                label: category.name,
                value: category.id,
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

export default BookmarkPage
