import { useState, useEffect } from 'react';
import { Table, Card, Typography, Space, Tag, Image } from 'antd';
import { contentApi } from '@/services/api';
import type { PageResult } from '@/services/types';

const { Title } = Typography;

interface TimelineItem {
  itemId: number;
  itemType: 'VIDEO' | 'MOVEMENT';
  userId: number;
  nickname: string;
  avatar: string;
  textContent: string;
  medias?: Array<{
    type: 'IMAGE' | 'VIDEO';
    url: string;
    thumbnail?: string;
  }>;
  likeCount: number;
  commentCount: number;
  isLiked: boolean;
  createdAt: string;
}

const Timeline: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<TimelineItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);

  useEffect(() => {
    fetchData();
  }, [page, pageSize]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await contentApi.timeline({ pageNo: page, pageSize });
      const records = (res.data as PageResult<TimelineItem>).records || res.data.list || [];
      setData(records);
      setTotal(res.data.total || 0);
    } catch (error) {
      console.error('Failed to fetch timeline:', error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: '用户',
      key: 'user',
      width: 200,
      render: (_: unknown, record: TimelineItem) => (
        <Space>
          <Image
            src={record.avatar}
            width={40}
            height={40}
            style={{ borderRadius: '50%' }}
            fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
          />
          <div>
            <div>{record.nickname}</div>
            <div style={{ fontSize: 12, color: '#999' }}>ID: {record.userId}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '内容类型',
      dataIndex: 'itemType',
      key: 'itemType',
      width: 100,
      render: (type: 'VIDEO' | 'MOVEMENT') => (
        <Tag color={type === 'VIDEO' ? 'red' : 'green'}>
          {type === 'VIDEO' ? '视频' : '图文'}
        </Tag>
      ),
    },
    {
      title: '文本内容',
      dataIndex: 'textContent',
      key: 'textContent',
      ellipsis: true,
    },
    {
      title: '媒体',
      key: 'medias',
      width: 150,
      render: (record: TimelineItem) => {
        if (!record.medias || record.medias.length === 0) return '-';
        return (
          <Space>
            {record.medias.slice(0, 3).map((media, index) => (
              <Image
                key={index}
                src={media.thumbnail || media.url}
                width={40}
                height={40}
                style={{ borderRadius: 4 }}
              />
            ))}
            {record.medias.length > 3 && (
              <div style={{ fontSize: 12, color: '#999' }}>+{record.medias.length - 3}</div>
            )}
          </Space>
        );
      },
    },
    {
      title: '互动',
      key: 'interaction',
      width: 120,
      render: (record: TimelineItem) => (
        <Space>
          <Tag>👍 {record.likeCount}</Tag>
          <Tag>💬 {record.commentCount}</Tag>
        </Space>
      ),
    },
    {
      title: '发布时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (date: string) => new Date(date).toLocaleString(),
    },
  ];

  return (
    <div>
      <Title level={2}>时间线管理</Title>
      
      <Card>
        <Table
          columns={columns}
          dataSource={data}
          rowKey="itemId"
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize || 20);
            },
          }}
          scroll={{ x: 1200 }}
        />
      </Card>
    </div>
  );
};

export default Timeline;
