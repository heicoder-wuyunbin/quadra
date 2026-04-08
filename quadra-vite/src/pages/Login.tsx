import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, message, Typography, Checkbox } from 'antd';
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { adminApi } from '@/services/api';
import { setToken } from '@/utils/storage';
import type { AdminLoginRequest } from '@/services/types';

const { Title } = Typography;

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: AdminLoginRequest) => {
    setLoading(true);
    try {
      const { accessToken, refreshToken } = await adminApi.login(values);
      setToken(accessToken, refreshToken);
      // 存储登录时间，用于判断是否是刚刚登录的请求
      localStorage.setItem('login_time', Date.now().toString());
      message.success('登录成功');
      navigate('/');
    } catch (error: unknown) {
      console.error('Login failed:', error);
      message.error((error as Error)?.message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#f0f2f5',
    }}>
      <Card
        style={{
          width: 400,
          borderRadius: 8,
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)',
        }}
        styles={{ body: { padding: '24px 32px' } }}
      >
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{
            width: 48,
            height: 48,
            margin: '0 auto 16px',
            background: '#1890ff',
            borderRadius: 12,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <SafetyCertificateOutlined style={{ fontSize: 28, color: 'white' }} />
          </div>
          <Title level={3} style={{ marginBottom: 8, fontWeight: 600, fontSize: 20 }}>
            登录
          </Title>
        </div>

        <Form
          name="login"
          initialValues={{ remember: true, username: 'admin', password: '123456' }}
          onFinish={onFinish}
          size="large"
          layout="vertical"
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined style={{ color: '#bfbfbf' }} />}
              placeholder="用户名"
              size="large"
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined style={{ color: '#bfbfbf' }} />}
              placeholder="密码"
              size="large"
            />
          </Form.Item>

          <Form.Item>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <Checkbox defaultChecked>记住我</Checkbox>
              <a href="#" style={{ color: '#1890ff' }}>忘记密码？</a>
            </div>
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              size="large"
              block
              style={{
                height: 40,
                fontSize: 16,
              }}
            >
              登 录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Login;
