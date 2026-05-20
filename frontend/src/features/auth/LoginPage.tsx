import { Button, Card, Form, Input, Typography } from 'antd'

function LoginPage() {
  return (
    <Card className="auth-card">
      <Typography.Title level={3}>登录</Typography.Title>
      <Typography.Paragraph type="secondary">
        请输入账号信息以继续访问 Bookmark Hub。
      </Typography.Paragraph>
      <Form layout="vertical">
        <Form.Item label="用户名" name="username">
          <Input placeholder="请输入用户名" />
        </Form.Item>
        <Form.Item label="密码" name="password">
          <Input.Password placeholder="请输入密码" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" block>
            登录
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}

export default LoginPage
