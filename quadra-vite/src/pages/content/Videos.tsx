import { useState, useEffect, Key } from 'react';
import { Card, Table, Typography, Space, Button, Input, Form, message, Tag, Popconfirm, Modal, Select, Breadcrumb } from 'antd';
import { SearchOutlined, PlayCircleOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;

interface VideoRecord {
  id: number;
  userId: number;
  userNickname: string;
  userAvatar?: string;
  title: string;
  description?: string;
  coverUrl?: string;
  videoUrl: string;
  duration: number;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  status: number;
  reportCount: number;
  createdAt: string;
}

interface VideoQueryParams {
  page: number;
  size: number;
  userId?: string;
  status?: number;
}

const Videos: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<VideoRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchUserId, setSearchUserId] = useState('');
  const [statusFilter, setStatusFilter] = useState<number | undefined>();
  const [previewVisible, setPreviewVisible] = useState(false);
  const [currentVideo, setCurrentVideo] = useState<string>('');
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    fixed: 'left',
    columnWidth: 50,
  };

  const fetchData = async (params: VideoQueryParams = { page, size: pageSize }) => {
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
      // const response = await contentApi.getVideos(params);
      // 模拟数据
      const mockData: VideoRecord[] = [
        {
          id: 1,
          userId: 10001,
          userNickname: '张三',
          userAvatar: 'https://via.placeholder.com/50',
          title: '美丽的风景',
          description: '分享一段美丽的风景视频',
          coverUrl: 'https://via.placeholder.com/300x200',
          videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
          duration: 125,
          likeCount: 520,
          commentCount: 89,
          shareCount: 32,
          status: 1,
          reportCount: 0,
          createdAt: '2024-01-15 14:30:00',
        },
        {
          id: 2,
          userId: 10002,
          userNickname: '李四',
          userAvatar: 'https://via.placeholder.com/50',
          title: '美食制作教程',
          description: '教大家做一道家常菜',
          coverUrl: 'https://via.placeholder.com/300x200',
          videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
          duration: 300,
          likeCount: 1024,
          commentCount: 156,
          shareCount: 78,
          status: 0,
          reportCount: 2,
          createdAt: '2024-01-15 13:20:00',
        },
      ];
      
      setData(mockData);
      setTotal(2);
    } catch (error) {
      message.error('获取视频列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = () => {
    setPage(1);
    fetchData({ page: 1, size: pageSize, userId: searchUserId || undefined, status: statusFilter });
  };

  const handleReset = () => {
    setSearchUserId('');
    setStatusFilter(undefined);
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const handleApprove = async (id: number) => {
    try {
      // TODO: 调用审核通过 API
      message.success('审核通过');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleReject = async (id: number) => {
    try {
      // TODO: 调用审核拒绝 API
      message.success('已拒绝');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handlePreviewVideo = (videoUrl: string) => {
    setCurrentVideo(videoUrl);
    setPreviewVisible(true);
  };

  const getStatusTag = (status: number) => {
    switch (status) {
      case 0:
        return <Tag color="orange">待审核</Tag>;
      case 1:
        return <Tag color="green">已通过</Tag>;
      case 2:
        return <Tag color="red">已拒绝</Tag>;
      default:
        return <Tag>未知</Tag>;
    }
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const columns: ColumnsType<VideoRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      fixed: 'left',
    },
    {
      title: '用户 ID',
      dataIndex: 'userId',
      key: 'userId',
      width: 100,
      render: (id: number) => <Text copyable>{id}</Text>,
    },
    {
      title: '用户昵称',
      dataIndex: 'userNickname',
      key: 'userNickname',
      width: 120,
      render: (_: any, record: VideoRecord) => (
        <Space>
          {record.userAvatar && (
            <img src={record.userAvatar} alt="" style={{ width: 32, height: 32, borderRadius: '50%' }} />
          )}
          <span>{record.userNickname}</span>
        </Space>
      ),
    },
    {
      title: '视频信息',
      key: 'video',
      width: 250,
      render: (_: any, record: VideoRecord) => (
        <Space>
          {record.coverUrl && (
            <img src={record.coverUrl} alt="" style={{ width: 120, height: 68, borderRadius: 4, objectFit: 'cover' }} />
          )}
          <div style={{ maxWidth: 120 }}>
            <div style={{ fontWeight: 500, marginBottom: 4 }}>{record.title}</div>
            <div style={{ fontSize: 12, color: '#999' }}>{formatDuration(record.duration)}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '互动',
      key: 'interactions',
      width: 150,
      render: (_: any, record: VideoRecord) => (
        <Space size="small">
          <Tag>👍 {record.likeCount}</Tag>
          <Tag>💬 {record.commentCount}</Tag>
          <Tag>🔗 {record.shareCount}</Tag>
        </Space>
      ),
    },
    {
      title: '举报',
      dataIndex: 'reportCount',
      key: 'reportCount',
      width: 80,
      render: (count: number) => (
        <Tag color={count > 0 ? 'red' : 'default'}>
          {count}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => getStatusTag(status),
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
      width: 200,
      fixed: 'right',
      render: (_: any, record: VideoRecord) => (
        <Space size="small">
          <Button 
            type="link" 
            size="small" 
            icon={<PlayCircleOutlined />}
            onClick={() => handlePreviewVideo(record.videoUrl)}
          >
            播放
          </Button>
          {record.status === 0 && (
            <>
              <Popconfirm
                title="确认通过"
                description="确定要通过这个视频吗？"
                onConfirm={() => handleApprove(record.id)}
                okText="确认"
                cancelText="取消"
              >
                <Button type="link" size="small" icon={<CheckOutlined />} style={{ color: '#52c41a' }}>
                  通过
                </Button>
              </Popconfirm>
              <Popconfirm
                title="确认拒绝"
                description="确定要拒绝这个视频吗？"
                onConfirm={() => handleReject(record.id)}
                okText="确认"
                cancelText="取消"
              >
                <Button type="link" size="small" icon={<CloseOutlined />} danger>
                  拒绝
                </Button>
              </Popconfirm>
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '内容审核' },
        { title: '视频审核' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>视频审核</Title>
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
                <Option value={0}>待审核</Option>
                <Option value={1}>已通过</Option>
                <Option value={2}>已拒绝</Option>
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
          scroll={{ x: 1400 }}
          size="middle"
        />
      </Card>

      <Modal
        title="视频预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={null}
        width={800}
      >
        <video 
          src={currentVideo} 
          controls 
          style={{ width: '100%', maxHeight: '500px' }}
          autoPlay
        >
          您的浏览器不支持视频播放
        </video>
      </Modal>
    </div>
  );
};

export default Videos;
