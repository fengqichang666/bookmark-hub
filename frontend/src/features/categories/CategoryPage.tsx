import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Card, Col, Form, Input, Row, Select, Tree, Typography } from 'antd'
import { httpClient, type PageResponse } from '../../api/http'

type CategoryItem = {
  id: number
  name: string
  parentId: number | null
}

type CategoryTreeNode = {
  key: number
  title: string
  children: CategoryTreeNode[]
}

type CategoryFormValues = {
  name: string
  parentId?: number
}

function buildTreeData(categories: CategoryItem[]) {
  const nodeMap = new Map<number, CategoryTreeNode>()
  const roots: CategoryTreeNode[] = []

  categories.forEach((category) => {
    nodeMap.set(category.id, { key: category.id, title: category.name, children: [] })
  })

  categories.forEach((category) => {
    const node = nodeMap.get(category.id)
    if (!node) {
      return
    }

    if (category.parentId == null) {
      roots.push(node)
      return
    }

    const parentNode = nodeMap.get(category.parentId)
    if (parentNode) {
      parentNode.children.push(node)
    } else {
      roots.push(node)
    }
  })

  return roots
}

function CategoryPage() {
  const queryClient = useQueryClient()
  const [form] = Form.useForm<CategoryFormValues>()
  const { data } = useQuery({
    queryKey: ['categories'],
    queryFn: async () => {
      const response = await httpClient.get<PageResponse<CategoryItem>>('/categories')
      return response.data.items
    },
  })

  const createCategory = useMutation({
    mutationFn: async (values: CategoryFormValues) => {
      await httpClient.post('/categories', {
        name: values.name,
        parentId: values.parentId ?? null,
      })
    },
    onSuccess: async () => {
      form.resetFields()
      await queryClient.invalidateQueries({ queryKey: ['categories'] })
    },
  })

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24} lg={14}>
        <Card>
          <Typography.Title level={3}>分类管理</Typography.Title>
          <Tree treeData={buildTreeData(data ?? [])} />
        </Card>
      </Col>
      <Col xs={24} lg={10}>
        <Card>
          <Typography.Title level={4}>新增分类</Typography.Title>
          <Form<CategoryFormValues> form={form} layout="vertical" onFinish={(values) => createCategory.mutate(values)}>
            <Form.Item label="分类名称" name="name" rules={[{ required: true, message: '请输入分类名称' }]}>
              <Input placeholder="例如：前端" />
            </Form.Item>
            <Form.Item label="上级分类" name="parentId">
              <Select
                allowClear
                placeholder="可选"
                options={(data ?? []).map((category) => ({
                  label: category.name,
                  value: category.id,
                }))}
              />
            </Form.Item>
            <Button type="primary" htmlType="submit" loading={createCategory.isPending}>
              保存分类
            </Button>
          </Form>
        </Card>
      </Col>
    </Row>
  )
}

export default CategoryPage
