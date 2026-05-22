import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Card, Col, Form, Input, Row, Select, Table, Typography } from 'antd'
import { httpClient, type PageResponse } from '../../api/http'

type MemberItem = {
  id: number
  username: string
  displayName: string
  role: string
}

type MemberFormValues = {
  username: string
  password: string
  displayName: string
  role: string
}

function MemberPage() {
  const queryClient = useQueryClient()
  const [form] = Form.useForm<MemberFormValues>()
  const { data, isPending } = useQuery({
    queryKey: ['members'],
    queryFn: async () => {
      const response = await httpClient.get<PageResponse<MemberItem>>('/members')
      return response.data.items
    },
  })

  const createMember = useMutation({
    mutationFn: async (values: MemberFormValues) => {
      await httpClient.post('/members', values)
    },
    onSuccess: async () => {
      form.resetFields()
      await queryClient.invalidateQueries({ queryKey: ['members'] })
    },
  })

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24} lg={14}>
        <Card>
          <Typography.Title level={3}>团队成员</Typography.Title>
          <Table<MemberItem>
            rowKey="id"
            loading={isPending}
            pagination={false}
            dataSource={data ?? []}
            columns={[
              { title: '用户名', dataIndex: 'username' },
              { title: '显示名', dataIndex: 'displayName' },
              { title: '角色', dataIndex: 'role' },
            ]}
          />
        </Card>
      </Col>
      <Col xs={24} lg={10}>
        <Card>
          <Typography.Title level={4}>新增成员</Typography.Title>
          <Form<MemberFormValues> form={form} layout="vertical" onFinish={(values) => createMember.mutate(values)}>
            <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
              <Input />
            </Form.Item>
            <Form.Item label="密码" name="password" rules={[{ required: true, message: '请输入密码' }]}>
              <Input.Password />
            </Form.Item>
            <Form.Item label="显示名" name="displayName" rules={[{ required: true, message: '请输入显示名' }]}>
              <Input />
            </Form.Item>
            <Form.Item label="角色" name="role" initialValue="MEMBER" rules={[{ required: true, message: '请选择角色' }]}>
              <Select
                options={[
                  { label: '管理员', value: 'ADMIN' },
                  { label: '成员', value: 'MEMBER' },
                ]}
              />
            </Form.Item>
            <Button type="primary" htmlType="submit" loading={createMember.isPending}>
              创建成员
            </Button>
          </Form>
        </Card>
      </Col>
    </Row>
  )
}

export default MemberPage
