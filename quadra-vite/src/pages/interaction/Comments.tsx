import { useState, useEffect, useCallback, Key } from 'react';
import { Card, Table, Typography, Space, Button, Input, Form, message, Tag, Select, Popconfirm, Breadcrumb, Modal, Descriptions } from 'antd';
import { SearchOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { interactionAdminApi } from '@/services/api';
import type { PageResult } from '@/services/types';

const { Title } = Typography;
const { Option } = Select;

interface CommentRecord {
  id: number;
  userId: number;
  userNickname: string;
  userAvatar?: string;
  targetType: 'MOVEMENT' | 'VIDEO';
  targetId: number;
  targetTitle?: string;
  content: string;
  likeCount: number;
  replyCount: number;
  status: 'VISIBLE' | 'HIDDEN' | 'DELETED';
  isTop: boolean;
  createdAt: string;
  updatedAt?: string;
}

interface CommentQueryParams {
  page: number;
  size: number;
  userId?: string;
  targetType?: string;
  status?: string;
}

const Comments: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<CommentRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchUserId, setSearchUserId] = useState('');
  const [targetTypeFilter, setTargetTypeFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentComment, setCurrentComment] = useState<CommentRecord | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    fixed: 'left',
    columnWidth: 50,
  };

  const fetchData = useCallback(async (params: CommentQueryParams = { page, size: pageSize }) => {
    // 检查是否已登录
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const payload = (await interactionAdminApi.listComments(params)) as PageResult<CommentRecord>;
      setData(payload.records || payload.list || []);
      setTotal(payload.total || 0);
    } catch (error) {
      message.error((error as Error)?.message || '获取评论列表失败');
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
      targetType: targetTypeFilter || undefined,
      status: statusFilter || undefined,
    });
  };

  const handleReset = () => {
    setSearchUserId('');
    setTargetTypeFilter(undefined);
    setStatusFilter(undefined);
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const handleDelete = async (id: number) => {
    try {
      await interactionAdminApi.deleteComment(id);
      message.success('已删除评论');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const handleViewDetail = (record: CommentRecord) => {
    setCurrentComment(record);
    setDetailVisible(true);
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; label: string }> = {
      VISIBLE: { color: 'green', label: '显示中' },
      HIDDEN: { color: 'orange', label: '已隐藏' },
      DELETED: { color: 'red', label: '已删除' },
    };
    const config = statusMap[status] || { color: 'default', label: status };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const getTargetTypeTag = (type: string) => {
    const typeMap: Record<string, { color: string; label: string }> = {
      MOVEMENT: { color: 'blue', label: '动态' },
      VIDEO: { color: 'purple', label: '视频' },
    };
    const config = typeMap[type] || { color: 'default', label: type };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const columns: ColumnsType<CommentRecord> = [
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
      render: (_: unknown, record: CommentRecord) => (
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
      title: '评论内容',
      dataIndex: 'content',
      key: 'content',
      width: 300,
      ellipsis: true,
    },
    {
      title: '目标类型',
      dataIndex: 'targetType',
      key: 'targetType',
      width: 100,
      render: (type: string) => getTargetTypeTag(type),
    },
    {
      title: '目标',
      key: 'target',
      width: 150,
      render: (_: unknown, record: CommentRecord) => (
        <div>
          <div style={{ fontSize: 14, marginBottom: 4 }}>{record.targetTitle || `ID: ${record.targetId}`}</div>
          <div style={{ fontSize: 12, color: '#999' }}>ID: {record.targetId}</div>
        </div>
      ),
    },
    {
      title: '互动',
      key: 'interactions',
      width: 120,
      render: (_: unknown, record: CommentRecord) => (
        <Space size="small">
          <Tag>👍 {record.likeCount}</Tag>
          <Tag>💬 {record.replyCount}</Tag>
        </Space>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '置顶',
      dataIndex: 'isTop',
      key: 'isTop',
      width: 80,
      render: (isTop: boolean) => (
        <Tag color={isTop ? 'red' : 'default'}>
          {isTop ? '是' : '否'}
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
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_: unknown, record: CommentRecord) => (
        <Space size="small">
          <Button 
            type="link" 
            size="small" 
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            详情
          </Button>
          {record.status !== 'DELETED' && (
            <Popconfirm
              title="确认删除"
              description="确定要删除这条评论吗？"
              onConfirm={() => handleDelete(record.id)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="link" size="small" icon={<DeleteOutlined />} danger>
                删除
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '互动管理' },
        { title: '评论管理' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>评论管理</Title>
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
            <Form.Item label="目标类型">
              <Select
                placeholder="全部类型"
                value={targetTypeFilter}
                onChange={setTargetTypeFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="MOVEMENT">动态</Option>
                <Option value="VIDEO">视频</Option>
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
                <Option value="VISIBLE">显示中</Option>
                <Option value="HIDDEN">已隐藏</Option>
                <Option value="DELETED">已删除</Option>
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

      <Modal
        title="评论详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        {currentComment && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="评论 ID">{currentComment.id}</Descriptions.Item>
            <Descriptions.Item label="用户">{currentComment.userNickname} (ID: {currentComment.userId})</Descriptions.Item>
            <Descriptions.Item label="目标类型">{getTargetTypeTag(currentComment.targetType)}</Descriptions.Item>
            <Descriptions.Item label="目标 ID">{currentComment.targetId}</Descriptions.Item>
            <Descriptions.Item label="目标标题">{currentComment.targetTitle || '-'}</Descriptions.Item>
            <Descriptions.Item label="评论内容">
              <div style={{ 
                background: '#f5f5f5', 
                padding: '12px', 
                borderRadius: '4px',
                whiteSpace: 'pre-wrap'
              }}>
                {currentComment.content}
              </div>
            </Descriptions.Item>
            <Descriptions.Item label="点赞数">{currentComment.likeCount}</Descriptions.Item>
            <Descriptions.Item label="回复数">{currentComment.replyCount}</Descriptions.Item>
            <Descriptions.Item label="状态">{getStatusTag(currentComment.status)}</Descriptions.Item>
            <Descriptions.Item label="是否置顶">
              <Tag color={currentComment.isTop ? 'red' : 'default'}>
                {currentComment.isTop ? '是' : '否'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">{dayjs(currentComment.createdAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
            {currentComment.updatedAt && (
              <Descriptions.Item label="更新时间">{dayjs(currentComment.updatedAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Comments;
