import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Tag, Space, Button, Typography, message, Spin, Divider, Table, Collapse } from 'antd';
import { ArrowLeftOutlined, UserOutlined, SafetyCertificateOutlined, EnvironmentOutlined } from '@ant-design/icons';
import { userApi } from '@/services/api';
import type { UserDetailDTO } from '@/services/types';

const { Title, Text } = Typography;
const { Panel } = Collapse;

const UserProfile: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState<UserDetailDTO | null>(null);

  useEffect(() => {
    if (id) {
      fetchUserDetail(id);
    }
  }, [id]);

  const fetchUserDetail = async (userId: string) => {
    setLoading(true);
    try {
      const response = await userApi.getUserDetail(userId);
      if (response.data) {
        setUser(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch user detail:', error);
      message.error('获取用户详情失败');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" tip="加载中..." />
      </div>
    );
  }

  if (!user) {
    return (
      <Card>
        <Title level={2}>用户不存在</Title>
        <Button type="primary" onClick={() => navigate('/user/users')}>
          返回列表
        </Button>
      </Card>
    );
  }

  // 解析 JSON 字段
  const parseJsonField = (field: any): string => {
    if (!field) return '-';
    if (typeof field === 'string') {
      try {
        return JSON.stringify(JSON.parse(field), null, 2);
      } catch {
        return field;
      }
    }
    if (typeof field === 'object') {
      return JSON.stringify(field, null, 2);
    }
    return String(field);
  };

  // 性别显示
  const genderText = (gender?: number) => {
    if (gender === 1) return '男';
    if (gender === 2) return '女';
    return '未知';
  };

  // 婚姻状况显示
  const marriageText = (marriage?: number) => {
    if (marriage === 1) return '未婚';
    if (marriage === 2) return '已婚';
    if (marriage === 3) return '离异';
    if (marriage === 4) return '丧偶';
    return '未知';
  };

  // 通知设置显示
  const notificationText = (value?: number) => {
    if (value === 1) return '开启';
    if (value === 0) return '关闭';
    return '未知';
  };

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/user/users')}>
            返回
          </Button>
          <Title level={2} style={{ margin: 0 }}>用户详情</Title>
        </Space>
      </Card>

      <Card title="基本信息" style={{ marginBottom: 16 }}>
        <Descriptions column={3} bordered>
          <Descriptions.Item label="用户 ID" span={3}>
            <Text strong>{user.id}</Text>
          </Descriptions.Item>
          
          <Descriptions.Item label="手机号">
            <Space>
              <UserOutlined />
              {user.mobile}
            </Space>
          </Descriptions.Item>
          
          <Descriptions.Item label="昵称">
            {user.nickname || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="性别">
            {genderText(user.gender)}
          </Descriptions.Item>
          
          <Descriptions.Item label="生日">
            {user.birthday || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="城市">
            <Space>
              <EnvironmentOutlined />
              {user.city || '-'}
            </Space>
          </Descriptions.Item>
          
          <Descriptions.Item label="收入">
            {user.income || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="职业">
            {user.profession || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="婚姻状况">
            {marriageText(user.marriage)}
          </Descriptions.Item>
          
          <Descriptions.Item label="状态">
            <Tag color={user.status === 1 ? 'green' : 'red'}>
              {user.status === 1 ? '正常' : '禁用'}
            </Tag>
          </Descriptions.Item>
          
          <Descriptions.Item label="创建时间">
            {user.createdAt ? new Date(user.createdAt).toLocaleString('zh-CN') : '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="更新时间">
            {user.updatedAt ? new Date(user.updatedAt).toLocaleString('zh-CN') : '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="详细信息" style={{ marginBottom: 16 }}>
        <Collapse>
          <Panel header="头像和封面" key="1">
            <Descriptions column={2} bordered>
              <Descriptions.Item label="头像" span={2}>
                {user.avatar ? (
                  <img src={user.avatar} alt="头像" style={{ maxWidth: '200px', borderRadius: '8px' }} />
                ) : (
                  '-'
                )}
              </Descriptions.Item>
              <Descriptions.Item label="封面图" span={2}>
                {user.coverPic ? (
                  <img src={user.coverPic} alt="封面" style={{ maxWidth: '400px', borderRadius: '8px' }} />
                ) : (
                  '-'
                )}
              </Descriptions.Item>
            </Descriptions>
          </Panel>
          
          <Panel header="标签信息" key="2">
            <Space wrap>
              {user.tags ? (
                user.tags.split(',').map((tag: string, index: number) => (
                  <Tag key={index}>{tag.trim()}</Tag>
                ))
              ) : (
                <Text type="secondary">暂无标签</Text>
              )}
            </Space>
          </Panel>
          
          <Panel header="通知设置" key="3">
            <Descriptions column={3} bordered>
              <Descriptions.Item label="点赞通知">
                <Tag color={user.likeNotification === 1 ? 'green' : 'default'}>
                  {notificationText(user.likeNotification)}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="评论通知">
                <Tag color={user.commentNotification === 1 ? 'green' : 'default'}>
                  {notificationText(user.commentNotification)}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="系统通知">
                <Tag color={user.systemNotification === 1 ? 'green' : 'default'}>
                  {notificationText(user.systemNotification)}
                </Tag>
              </Descriptions.Item>
            </Descriptions>
          </Panel>
          
          <Panel header="原始数据（JSON）" key="4">
            <pre style={{ 
              background: '#f5f5f5', 
              padding: '16px', 
              borderRadius: '4px',
              overflow: 'auto',
              fontSize: '12px',
              maxHeight: '400px'
            }}>
              {parseJsonField(user)}
            </pre>
          </Panel>
        </Collapse>
      </Card>

      <Card title="快捷操作" style={{ marginBottom: 16 }}>
        <Space>
          <Button type="primary" danger>
            禁用用户
          </Button>
          <Button type="primary">
            重置密码
          </Button>
          <Button>
            查看黑名单
          </Button>
        </Space>
      </Card>
    </div>
  );
};

export default UserProfile;
