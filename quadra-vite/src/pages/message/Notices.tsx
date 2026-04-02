import { useState, useEffect } from 'react';
import { Card, Table, Typography, Space, Button, Input, Form, message, Tag, Select, Popconfirm, Breadcrumb, Modal, Descriptions, Badge } from 'antd';
import { SearchOutlined, MailOutlined, SendOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;

interface NoticeRecord {
  id: number;
  title: string;
  content: string;
  type: 'SYSTEM' | 'ACTIVITY' | 'REMINDER' | 'CUSTOM';
  targetType: 'ALL' | 'USER' | 'GROUP';
  targetIds?: number[];
  senderId: number;
  senderName: string;
  isRead: boolean;
  readCount: number;
  status: 'DRAFT' | 'SENT' | 'DELETED';
  priority: 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';
  scheduledAt?: string;
  sentAt?: string;
  createdAt: string;
}

interface NoticeQueryParams {
  page: number;
  size: number;
  type?: string;
  status?: string;
  priority?: string;
}

const Notices: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<NoticeRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [typeFilter, setTypeFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [priorityFilter, setPriorityFilter] = useState<string | undefined>();
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentNotice, setCurrentNotice] = useState<NoticeRecord | null>(null);

  const fetchData = async (params: NoticeQueryParams = { page, size: pageSize }) => {
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
      // const response = await messageApi.getNotices(params);
      // 模拟数据
      const mockData: NoticeRecord[] = [
        {
          id: 1,
          title: '系统维护通知',
          content: '尊敬的用户，我们将于今晚 23:00-01:00 进行系统维护，届时部分功能可能无法正常使用。给您带来的不便敬请谅解。',
          type: 'SYSTEM',
          targetType: 'ALL',
          senderId: 1,
          senderName: '系统管理员',
          isRead: true,
          readCount: 1024,
          status: 'SENT',
          priority: 'HIGH',
          sentAt: '2024-01-15 10:00:00',
          createdAt: '2024-01-15 09:30:00',
        },
        {
          id: 2,
          title: '新用户福利',
          content: '欢迎加入！新用户注册即可享受 7 天 VIP 会员体验，快来体验更多功能吧！',
          type: 'ACTIVITY',
          targetType: 'USER',
          targetIds: [10001, 10002, 10003],
          senderId: 2,
          senderName: '运营专员',
          isRead: false,
          readCount: 520,
          status: 'SENT',
          priority: 'NORMAL',
          sentAt: '2024-01-15 14:00:00',
          createdAt: '2024-01-15 13:30:00',
        },
        {
          id: 3,
          title: '账号安全提醒',
          content: '检测到您的账号在异地登录，如非本人操作，请及时修改密码。',
          type: 'REMINDER',
          targetType: 'USER',
          targetIds: [10004],
          senderId: 1,
          senderName: '系统管理员',
          isRead: true,
          readCount: 1,
          status: 'SENT',
          priority: 'URGENT',
          sentAt: '2024-01-15 16:00:00',
          createdAt: '2024-01-15 15:50:00',
        },
        {
          id: 4,
          title: '春节活动预告',
          content: '春节期间将举办线上交友活动，丰厚奖励等你来拿！敬请期待！',
          type: 'ACTIVITY',
          targetType: 'ALL',
          senderId: 2,
          senderName: '运营专员',
          isRead: false,
          readCount: 0,
          status: 'DRAFT',
          priority: 'LOW',
          scheduledAt: '2024-01-20 10:00:00',
          createdAt: '2024-01-15 17:00:00',
        },
      ];
      
      setData(mockData);
      setTotal(mockData.length);
    } catch (error) {
      message.error('获取站内信列表失败');
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
      type: typeFilter || undefined,
      status: statusFilter || undefined,
      priority: priorityFilter || undefined,
    });
  };

  const handleReset = () => {
    setTypeFilter(undefined);
    setStatusFilter(undefined);
    setPriorityFilter(undefined);
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const handleDelete = async (id: number) => {
    try {
      // TODO: 调用删除站内信 API
      message.success('已删除站内信');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleViewDetail = (record: NoticeRecord) => {
    setCurrentNotice(record);
    setDetailVisible(true);
  };

  const getTypeTag = (type: string) => {
    const typeMap: Record<string, { color: string; label: string }> = {
      SYSTEM: { color: 'blue', label: '系统通知' },
      ACTIVITY: { color: 'green', label: '活动通知' },
      REMINDER: { color: 'orange', label: '提醒' },
      CUSTOM: { color: 'purple', label: '自定义' },
    };
    const config = typeMap[type] || { color: 'default', label: type };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; label: string }> = {
      DRAFT: { color: 'default', label: '草稿' },
      SENT: { color: 'green', label: '已发送' },
      DELETED: { color: 'red', label: '已删除' },
    };
    const config = statusMap[status] || { color: 'default', label: status };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const getPriorityTag = (priority: string) => {
    const priorityMap: Record<string, { color: string; label: string }> = {
      LOW: { color: 'default', label: '低' },
      NORMAL: { color: 'blue', label: '普通' },
      HIGH: { color: 'orange', label: '高' },
      URGENT: { color: 'red', label: '紧急' },
    };
    const config = priorityMap[priority] || { color: 'default', label: priority };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const getTargetTypeTag = (type: string) => {
    const typeMap: Record<string, { color: string; label: string }> = {
      ALL: { color: 'blue', label: '全体用户' },
      USER: { color: 'green', label: '指定用户' },
      GROUP: { color: 'purple', label: '用户组' },
    };
    const config = typeMap[type] || { color: 'default', label: type };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const columns: ColumnsType<NoticeRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      fixed: 'left',
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      width: 250,
      ellipsis: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: string) => getTypeTag(type),
    },
    {
      title: '发送对象',
      dataIndex: 'targetType',
      key: 'targetType',
      width: 120,
      render: (type: string) => getTargetTypeTag(type),
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 100,
      render: (priority: string) => getPriorityTag(priority),
    },
    {
      title: '阅读数',
      dataIndex: 'readCount',
      key: 'readCount',
      width: 100,
      render: (count: number) => <Text>{count}</Text>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '发送时间',
      dataIndex: 'sentAt',
      key: 'sentAt',
      width: 180,
      render: (sentAt?: string) => sentAt ? dayjs(sentAt).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_: any, record: NoticeRecord) => (
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
              description="确定要删除这封站内信吗？"
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
        { title: '消息推送' },
        { title: '站内信管理' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>站内信管理</Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px' }}>
          <Form layout="inline">
            <Form.Item label="类型">
              <Select
                placeholder="全部类型"
                value={typeFilter}
                onChange={setTypeFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="SYSTEM">系统通知</Option>
                <Option value="ACTIVITY">活动通知</Option>
                <Option value="REMINDER">提醒</Option>
                <Option value="CUSTOM">自定义</Option>
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
                <Option value="DRAFT">草稿</Option>
                <Option value="SENT">已发送</Option>
                <Option value="DELETED">已删除</Option>
              </Select>
            </Form.Item>
            <Form.Item label="优先级">
              <Select
                placeholder="全部优先级"
                value={priorityFilter}
                onChange={setPriorityFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="LOW">低</Option>
                <Option value="NORMAL">普通</Option>
                <Option value="HIGH">高</Option>
                <Option value="URGENT">紧急</Option>
              </Select>
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
                <Button type="primary" icon={<SendOutlined />}>
                  发送站内信
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </div>

        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
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
          scroll={{ x: 1400 }}
          size="middle"
        />
      </Card>

      <Modal
        title="站内信详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        {currentNotice && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="站内信 ID">{currentNotice.id}</Descriptions.Item>
            <Descriptions.Item label="标题">{currentNotice.title}</Descriptions.Item>
            <Descriptions.Item label="类型">{getTypeTag(currentNotice.type)}</Descriptions.Item>
            <Descriptions.Item label="发送对象">{getTargetTypeTag(currentNotice.targetType)}</Descriptions.Item>
            {currentNotice.targetIds && currentNotice.targetIds.length > 0 && (
              <Descriptions.Item label="目标用户 ID">
                {currentNotice.targetIds.map(id => (
                  <Tag key={id}>{id}</Tag>
                ))}
              </Descriptions.Item>
            )}
            <Descriptions.Item label="优先级">{getPriorityTag(currentNotice.priority)}</Descriptions.Item>
            <Descriptions.Item label="发件人">{currentNotice.senderName} (ID: {currentNotice.senderId})</Descriptions.Item>
            <Descriptions.Item label="内容">
              <div style={{ 
                background: '#f5f5f5', 
                padding: '12px', 
                borderRadius: '4px',
                whiteSpace: 'pre-wrap',
                maxHeight: '300px',
                overflow: 'auto'
              }}>
                {currentNotice.content}
              </div>
            </Descriptions.Item>
            <Descriptions.Item label="阅读数">{currentNotice.readCount}</Descriptions.Item>
            <Descriptions.Item label="状态">{getStatusTag(currentNotice.status)}</Descriptions.Item>
            {currentNotice.scheduledAt && (
              <Descriptions.Item label="计划发送时间">
                {dayjs(currentNotice.scheduledAt).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
            )}
            {currentNotice.sentAt && (
              <Descriptions.Item label="实际发送时间">
                {dayjs(currentNotice.sentAt).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
            )}
            <Descriptions.Item label="创建时间">
              {dayjs(currentNotice.createdAt).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Notices;
