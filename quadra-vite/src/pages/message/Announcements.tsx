import { useEffect, useState } from 'react';
import { Badge, Breadcrumb, Button, Card, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Table, Tag, Typography } from 'antd';
import { EditOutlined, EyeOutlined, PlusOutlined, PushpinOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { messageApi } from '@/services/api';
import type { AnnouncementDTO, AnnouncementQueryParams, AnnouncementStatus } from '@/services/types';

const { Title } = Typography;
const { TextArea } = Input;

const Announcements: React.FC = () => {
  const [form] = Form.useForm();
  const [editForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AnnouncementDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<AnnouncementQueryParams>({});

  const [editOpen, setEditOpen] = useState(false);
  const [editing, setEditing] = useState<AnnouncementDTO | null>(null);
  const [detailOpen, setDetailOpen] = useState(false);

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, pageSize, query]);

  const fetchData = async () => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      setData([]);
      setTotal(0);
      return;
    }

    setLoading(true);
    try {
      const payload = await messageApi.listAnnouncements({
        page,
        size: pageSize,
        status: query.status,
        keyword: query.keyword,
      });
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('listAnnouncements failed:', error);
      message.error((error as Error)?.message || '获取公告列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    const values = await form.validateFields();
    setPage(1);
    setQuery({
      status: values.status || undefined,
      keyword: values.keyword?.trim() || undefined,
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const openCreate = () => {
    setEditing(null);
    editForm.resetFields();
    editForm.setFieldsValue({ status: 'DRAFT' as AnnouncementStatus, isTop: false });
    setEditOpen(true);
  };

  const openEdit = (record: AnnouncementDTO) => {
    setEditing(record);
    editForm.setFieldsValue({
      title: record.title,
      content: record.content,
      status: record.status,
      isTop: record.isTop,
    });
    setEditOpen(true);
  };

  const saveAnnouncement = async () => {
    const values = await editForm.validateFields();
    try {
      if (editing) {
        await messageApi.updateAnnouncement(editing.id, values);
        message.success('公告已更新');
      } else {
        await messageApi.createAnnouncement(values);
        message.success('公告已创建');
      }
      setEditOpen(false);
      fetchData();
    } catch (error) {
      console.warn('saveAnnouncement failed:', error);
      message.error((error as Error)?.message || '保存失败');
    }
  };

  const handlePublishToggle = async (record: AnnouncementDTO) => {
    try {
      if (record.status === 'PUBLISHED') {
        await messageApi.offlineAnnouncement(record.id);
        message.success('已下架公告');
      } else {
        await messageApi.publishAnnouncement(record.id);
        message.success('已发布公告');
      }
      fetchData();
    } catch (error) {
      console.warn('publish toggle failed:', error);
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const handleTopToggle = async (record: AnnouncementDTO) => {
    try {
      await messageApi.toggleAnnouncementTop(record.id, !record.isTop);
      message.success(record.isTop ? '已取消置顶' : '已置顶');
      fetchData();
    } catch (error) {
      console.warn('toggleTop failed:', error);
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const getStatusTag = (status: AnnouncementStatus) => {
    const map: Record<AnnouncementStatus, { color: string; label: string }> = {
      DRAFT: { color: 'default', label: '草稿' },
      PUBLISHED: { color: 'green', label: '已发布' },
      OFFLINE: { color: 'red', label: '已下架' },
    };
    const cfg = map[status];
    return <Tag color={cfg.color}>{cfg.label}</Tag>;
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      width: 260,
      render: (t: string, r: AnnouncementDTO) => (
        <Space>
          {r.isTop && <Badge color="gold" text={<span style={{ fontSize: 12 }}>置顶</span>} />}
          <span>{t}</span>
        </Space>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      render: (s: AnnouncementStatus) => getStatusTag(s),
    },
    {
      title: '发布时间',
      dataIndex: 'publishedAt',
      key: 'publishedAt',
      width: 180,
      render: (v: string | undefined) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (v: string) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      fixed: 'right' as const,
      render: (_: unknown, record: AnnouncementDTO) => (
        <Space>
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => {
              setEditing(record);
              setDetailOpen(true);
            }}
          >
            详情
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title={record.status === 'PUBLISHED' ? '确认下架' : '确认发布'}
            okText="确认"
            cancelText="取消"
            onConfirm={() => handlePublishToggle(record)}
          >
            <Button type="link">
              {record.status === 'PUBLISHED' ? '下架' : '发布'}
            </Button>
          </Popconfirm>
          <Button type="link" icon={<PushpinOutlined />} onClick={() => handleTopToggle(record)}>
            {record.isTop ? '取消置顶' : '置顶'}
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '消息推送' }, { title: '系统公告' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          系统公告
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="status" label="状态">
              <Select placeholder="全部" allowClear style={{ width: 160 }}>
                <Select.Option value="DRAFT">草稿</Select.Option>
                <Select.Option value="PUBLISHED">已发布</Select.Option>
                <Select.Option value="OFFLINE">已下架</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="标题/内容" allowClear style={{ width: 260 }} />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
                <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
                  新建公告
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </div>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={data}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (t) => `共 ${t} 条`,
            onChange: (p, ps) => {
              setPage(p);
              setPageSize(ps);
            },
          }}
          scroll={{ x: 'max-content' }}
        />
      </Card>

      <Modal
        title={editing ? '编辑公告' : '新建公告'}
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={saveAnnouncement}
        okText="保存"
        cancelText="取消"
        width={860}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入公告标题' }]}>
            <Input maxLength={64} showCount />
          </Form.Item>
          <Form.Item name="content" label="内容" rules={[{ required: true, message: '请输入公告内容' }]}>
            <TextArea autoSize={{ minRows: 6, maxRows: 14 }} maxLength={5000} showCount />
          </Form.Item>
          <Space size={16} wrap style={{ width: '100%' }}>
            <Form.Item name="status" label="状态" style={{ width: 200 }}>
              <Select>
                <Select.Option value="DRAFT">草稿</Select.Option>
                <Select.Option value="PUBLISHED">已发布</Select.Option>
                <Select.Option value="OFFLINE">已下架</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="isTop" label="置顶" valuePropName="checked" style={{ width: 200 }}>
              <Switch checkedChildren="是" unCheckedChildren="否" />
            </Form.Item>
          </Space>
        </Form>
      </Modal>

      <Modal
        title="公告详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={760}
      >
        <div style={{ marginBottom: 8 }}>
          <strong>标题：</strong> {editing?.title || '-'}
        </div>
        <div style={{ marginBottom: 8 }}>
          <strong>状态：</strong> {editing ? getStatusTag(editing.status) : '-'}
        </div>
        <div style={{ background: '#f5f5f5', padding: 12, borderRadius: 6, whiteSpace: 'pre-wrap' }}>
          {editing?.content || '-'}
        </div>
      </Modal>
    </div>
  );
};

export default Announcements;
