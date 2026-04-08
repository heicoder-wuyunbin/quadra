import { useState, useEffect, useCallback, Key } from 'react';
import { Card, Table, Typography, Space, Button, Input, Form, message, Tag, Select, Badge, Breadcrumb } from 'antd';
import { SearchOutlined, HeartOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { socialAdminApi } from '@/services/api';
import type { PageResult } from '@/services/types';

const { Title, Text } = Typography;
const { Option } = Select;

interface MatchRecord {
  id: number;
  userId: number;
  userNickname: string;
  userAvatar?: string;
  matchedUserId: number;
  matchedUserNickname: string;
  matchedUserAvatar?: string;
  matchType: 'ALGORITHM' | 'USER_LIKE' | 'ACTIVITY';
  matchScore?: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED';
  isChatEnabled: boolean;
  createdAt: string;
  expiresAt?: string;
  acceptedAt?: string;
}

interface MatchQueryParams {
  page: number;
  size: number;
  userId?: string;
  matchType?: string;
  status?: string;
  dateRange?: string[];
}

const Matches: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<MatchRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchUserId, setSearchUserId] = useState('');
  const [matchTypeFilter, setMatchTypeFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    fixed: 'left',
    columnWidth: 50,
  };

  const fetchData = useCallback(async (params: MatchQueryParams = { page, size: pageSize }) => {
    // 检查是否已登录
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const payload = (await socialAdminApi.listMatches(params)) as PageResult<MatchRecord>;
      setData(payload.records || payload.list || []);
      setTotal(payload.total || 0);
    } catch (error) {
      message.error((error as Error)?.message || '获取匹配记录失败');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSearch = () => {
    setPage(1);
    fetchData({ 
      page: 1, 
      size: pageSize, 
      userId: searchUserId || undefined,
      matchType: matchTypeFilter || undefined,
      status: statusFilter || undefined,
    });
  };

  const handleReset = () => {
    setSearchUserId('');
    setMatchTypeFilter(undefined);
    setStatusFilter(undefined);
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const getMatchTypeTag = (type: string) => {
    const typeMap: Record<string, { color: string; label: string }> = {
      ALGORITHM: { color: 'blue', label: '算法推荐' },
      USER_LIKE: { color: 'pink', label: '用户喜欢' },
      ACTIVITY: { color: 'green', label: '活动匹配' },
    };
    const config = typeMap[type] || { color: 'default', label: type };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const getStatusBadge = (status: string) => {
    const statusMap: Record<string, { status: 'default' | 'processing' | 'success' | 'error'; text: string }> = {
      PENDING: { status: 'processing', text: '待处理' },
      ACCEPTED: { status: 'success', text: '已接受' },
      REJECTED: { status: 'error', text: '已拒绝' },
      EXPIRED: { status: 'default', text: '已过期' },
    };
    const config = statusMap[status] || { status: 'default', text: status };
    return <Badge status={config.status} text={config.text} />;
  };

  const columns: ColumnsType<MatchRecord> = [
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
      render: (_: unknown, record: MatchRecord) => (
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
      key: 'heart',
      width: 50,
      render: () => <HeartOutlined style={{ color: '#ff4d4f', fontSize: 16 }} />,
    },
    {
      title: '匹配用户',
      key: 'matchedUser',
      width: 200,
      render: (_: unknown, record: MatchRecord) => (
        <Space>
          {record.matchedUserAvatar && (
            <img src={record.matchedUserAvatar} alt="" style={{ width: 40, height: 40, borderRadius: '50%' }} />
          )}
          <div>
            <div style={{ fontWeight: 500 }}>{record.matchedUserNickname}</div>
            <div style={{ fontSize: 12, color: '#999' }}>ID: {record.matchedUserId}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '匹配类型',
      dataIndex: 'matchType',
      key: 'matchType',
      width: 120,
      render: (type: string) => getMatchTypeTag(type),
    },
    {
      title: '匹配度',
      dataIndex: 'matchScore',
      key: 'matchScore',
      width: 100,
      render: (score?: number) => score ? (
        <Text style={{ color: score >= 90 ? '#52c41a' : score >= 80 ? '#faad14' : '#ff4d4f', fontWeight: 500 }}>
          {score.toFixed(1)}%
        </Text>
      ) : '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusBadge(status),
    },
    {
      title: '聊天权限',
      dataIndex: 'isChatEnabled',
      key: 'isChatEnabled',
      width: 100,
      render: (enabled: boolean) => (
        <Tag color={enabled ? 'green' : 'default'}>
          {enabled ? '已开启' : '未开启'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => dayjs(createdAt).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '过期时间',
      dataIndex: 'expiresAt',
      key: 'expiresAt',
      width: 180,
      render: (expiresAt?: string) => expiresAt ? dayjs(expiresAt).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '社交管理' },
        { title: '匹配记录' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>匹配记录</Title>
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
            <Form.Item label="匹配类型">
              <Select
                placeholder="全部类型"
                value={matchTypeFilter}
                onChange={setMatchTypeFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="ALGORITHM">算法推荐</Option>
                <Option value="USER_LIKE">用户喜欢</Option>
                <Option value="ACTIVITY">活动匹配</Option>
              </Select>
            </Form.Item>
            <Form.Item label="状态">
              <Select
                placeholder="全部状态"
                value={statusFilter}
                onChange={setStatusFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="PENDING">待处理</Option>
                <Option value="ACCEPTED">已接受</Option>
                <Option value="REJECTED">已拒绝</Option>
                <Option value="EXPIRED">已过期</Option>
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

export default Matches;
