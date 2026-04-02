import { useState, useEffect, Key } from 'react';
import { Card, Table, Typography, Space, Button, Input, Form, message, Tag, Select, Popconfirm, Badge, Breadcrumb } from 'antd';
import { SearchOutlined, UsergroupAddOutlined, DeleteOutlined, BlockOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;

interface FriendshipRecord {
  id: number;
  userId: number;
  userNickname: string;
  userAvatar?: string;
  friendId: number;
  friendNickname: string;
  friendAvatar?: string;
  relationshipType: 'SINGLE' | 'DOUBLE';
  status: 'PENDING' | 'ACCEPTED' | 'BLOCKED';
  chatCount: number;
  lastChatAt?: string;
  createdAt: string;
}

interface FriendshipQueryParams {
  page: number;
  size: number;
  userId?: string;
  status?: string;
  relationshipType?: string;
}

const Friendships: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<FriendshipRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchUserId, setSearchUserId] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [relationshipTypeFilter, setRelationshipTypeFilter] = useState<string | undefined>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    fixed: 'left',
    columnWidth: 50,
  };

  const fetchData = async (params: FriendshipQueryParams = { page, size: pageSize }) => {
    // 检查是否已登录
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      // TODO: 替换为实际的 API 调用
      // const response = await socialApi.getFriendships(params);
      // 模拟数据
      const mockData: FriendshipRecord[] = [
        {
          id: 1,
          userId: 10001,
          userNickname: '张三',
          userAvatar: 'https://via.placeholder.com/50',
          friendId: 10002,
          friendNickname: '李四',
          friendAvatar: 'https://via.placeholder.com/50',
          relationshipType: 'DOUBLE',
          status: 'ACCEPTED',
          chatCount: 520,
          lastChatAt: '2024-01-15 14:30:00',
          createdAt: '2024-01-10 10:00:00',
        },
        {
          id: 2,
          userId: 10003,
          userNickname: '王五',
          userAvatar: 'https://via.placeholder.com/50',
          friendId: 10004,
          friendNickname: '赵六',
          friendAvatar: 'https://via.placeholder.com/50',
          relationshipType: 'SINGLE',
          status: 'PENDING',
          chatCount: 0,
          createdAt: '2024-01-15 13:20:00',
        },
        {
          id: 3,
          userId: 10005,
          userNickname: '孙七',
          userAvatar: 'https://via.placeholder.com/50',
          friendId: 10006,
          friendNickname: '周八',
          friendAvatar: 'https://via.placeholder.com/50',
          relationshipType: 'DOUBLE',
          status: 'BLOCKED',
          chatCount: 15,
          lastChatAt: '2024-01-14 12:00:00',
          createdAt: '2024-01-05 09:00:00',
        },
      ];
      
      setData(mockData);
      setTotal(mockData.length);
    } catch (error) {
      message.error('获取好友关系失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = () => {
    setPage(1);
    fetchData({ 
      page: 1, 
      size: pageSize, 
      userId: searchUserId || undefined,
      status: statusFilter || undefined,
      relationshipType: relationshipTypeFilter || undefined,
    });
  };

  const handleReset = () => {
    setSearchUserId('');
    setStatusFilter(undefined);
    setRelationshipTypeFilter(undefined);
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const handleBlock = async (id: number) => {
    try {
      // TODO: 调用拉黑 API
      message.success('已拉黑该好友');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      // TODO: 调用删除好友 API
      message.success('已删除好友关系');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('操作失败');
    }
  };

  const getStatusBadge = (status: string) => {
    const statusMap: Record<string, { status: 'default' | 'processing' | 'success' | 'error'; text: string }> = {
      PENDING: { status: 'processing', text: '待接受' },
      ACCEPTED: { status: 'success', text: '已接受' },
      BLOCKED: { status: 'error', text: '已拉黑' },
    };
    const config = statusMap[status] || { status: 'default', text: status };
    return <Badge status={config.status} text={config.text} />;
  };

  const getRelationshipTypeTag = (type: string) => {
    const typeMap: Record<string, { color: string; label: string }> = {
      SINGLE: { color: 'orange', label: '单向' },
      DOUBLE: { color: 'green', label: '双向' },
    };
    const config = typeMap[type] || { color: 'default', label: type };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const columns: ColumnsType<FriendshipRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      fixed: 'left',
    },
    {
      title: '用户',
      key: 'user',
      width: 200,
      render: (_: any, record: FriendshipRecord) => (
        <Space>
          {record.userAvatar && (
            <img src={record.userAvatar} alt="" style={{ width: 40, height: 40, borderRadius: '50%' }} />
          )}
          <div>
            <div style={{ fontWeight: 500 }}>{record.userNickname}</div>
            <div style={{ fontSize: 12, color: '#999' }}>ID: {record.userId}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '',
      key: 'icon',
      width: 50,
      render: () => <UsergroupAddOutlined style={{ color: '#1890ff', fontSize: 16 }} />,
    },
    {
      title: '好友',
      key: 'friend',
      width: 200,
      render: (_: any, record: FriendshipRecord) => (
        <Space>
          {record.friendAvatar && (
            <img src={record.friendAvatar} alt="" style={{ width: 40, height: 40, borderRadius: '50%' }} />
          )}
          <div>
            <div style={{ fontWeight: 500 }}>{record.friendNickname}</div>
            <div style={{ fontSize: 12, color: '#999' }}>ID: {record.friendId}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '关系类型',
      dataIndex: 'relationshipType',
      key: 'relationshipType',
      width: 100,
      render: (type: string) => getRelationshipTypeTag(type),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusBadge(status),
    },
    {
      title: '聊天次数',
      dataIndex: 'chatCount',
      key: 'chatCount',
      width: 100,
      render: (count: number) => <Text>{count} 次</Text>,
    },
    {
      title: '最后聊天',
      dataIndex: 'lastChatAt',
      key: 'lastChatAt',
      width: 180,
      render: (lastChatAt?: string) => lastChatAt ? dayjs(lastChatAt).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => dayjs(createdAt).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      fixed: 'right',
      render: (_: any, record: FriendshipRecord) => (
        <Space size="small">
          {record.status !== 'BLOCKED' && (
            <Popconfirm
              title="确认拉黑"
              description="确定要拉黑该好友吗？"
              onConfirm={() => handleBlock(record.id)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="link" size="small" icon={<BlockOutlined />} danger>
                拉黑
              </Button>
            </Popconfirm>
          )}
          <Popconfirm
            title="确认删除"
            description="确定要删除该好友关系吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" size="small" icon={<DeleteOutlined />} danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '社交管理' },
        { title: '好友关系' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>好友关系</Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px' }}>
          <Form layout="inline">
            <Form.Item label="用户 ID">
              <Input
                placeholder="请输入用户 ID"
                value={searchUserId}
                onChange={(e) => setSearchUserId(e.target.value)}
                onPressEnter={handleSearch}
                style={{ width: 150 }}
                prefix={<SearchOutlined />}
              />
            </Form.Item>
            <Form.Item label="状态">
              <Select
                placeholder="全部状态"
                value={statusFilter}
                onChange={setStatusFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="PENDING">待接受</Option>
                <Option value="ACCEPTED">已接受</Option>
                <Option value="BLOCKED">已拉黑</Option>
              </Select>
            </Form.Item>
            <Form.Item label="关系类型">
              <Select
                placeholder="全部类型"
                value={relationshipTypeFilter}
                onChange={setRelationshipTypeFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="SINGLE">单向</Option>
                <Option value="DOUBLE">双向</Option>
              </Select>
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
              </Space>
            </Form.Item>
          </Form>
        </div>

        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          rowSelection={rowSelection}
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            pageSizeOptions: ['10', '20', '50', '100'],
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize);
              fetchData({ page, size: pageSize });
            },
          }}
          scroll={{ x: 'max-content' }}
          size="middle"
        />
      </Card>
    </div>
  );
};

export default Friendships;
