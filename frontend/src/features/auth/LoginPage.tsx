import { startTransition, useState } from 'react'
import { Button, Card, Form, Input, Typography } from 'antd'
import { useNavigate } from 'react-router-dom'
import { httpClient } from '../../api/http'
import type { AuthUser } from './authStore'
import { useAuthStore } from './authStore'

type LoginFormValues = {
  username: string
  password: string
}

type LoginResponse = {
  token: string
  user: AuthUser
}

function LoginPage() {
  const navigate = useNavigate()
  const setSession = useAuthStore((state) => state.setSession)
  const [submitting, setSubmitting] = useState(false)

  async function handleFinish(values: LoginFormValues) {
    setSubmitting(true)
    try {
      const response = await httpClient.post<LoginResponse>('/auth/login', values)
      setSession(response.data.token, response.data.user)
      startTransition(() => {
        navigate('/')
      })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card className="auth-card">
      <Typography.Title level={3}>登录</Typography.Title>
      <Typography.Paragraph type="secondary">
        请输入账号信息以继续访问 Bookmark Hub。
      </Typography.Paragraph>
      <Form<LoginFormValues> layout="vertical" onFinish={handleFinish}>
        <Form.Item label="用户名" name="username">
          <Input placeholder="请输入用户名" />
        </Form.Item>
        <Form.Item label="密码" name="password">
          <Input.Password placeholder="请输入密码" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" block loading={submitting}>
            登录
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}

export default LoginPage
