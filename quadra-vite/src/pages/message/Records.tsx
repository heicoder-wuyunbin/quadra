import { useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, Form, Input, Select, Space, Statistic, Table, Tag, Typography, Row, Col } from 'antd';
import dayjs from 'dayjs';
import { messageApi } from '@/services/api';
import type { PageResult, PushRecordDTO, PushRecordQueryParams, PushStatus } from '@/services/types';

const { Title } = Typography;

const MessageRecords: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PushRecordDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<PushRecordQueryParams>({});

  const mockData: PushRecordDTO[] = useMemo(
    () => [
      {
        id: 1001,
        bizType: 'NOTICE',
        channel: 'IN_APP',
        title: '系统维护通知',
        targetCount: 12000,
        successCount: 12000,
        failCount: 0,
        readCount: 8421,
        status: 'SUCCESS',
        sentAt: '2024-01-15 10:00:00',
        createdAt: '2024-01-15 09:30:00',
      },
      {
        id: 1002,
        bizType: 'ANNOUNCEMENT',
        channel: 'IN_APP',
        title: '春节活动公告',
        targetCount: 12000,
        successCount: 11800,
        failCount: 200,
        readCount: 6500,
        status: 'PARTIAL',
        sentAt: '2024-01-10 10:00:00',
        createdAt: '2024-01-10 09:50:00',
      },
      {
        id: 1003,
        bizType: 'NOTICE',
        channel: 'PUSH',
        title: '账号安全提醒',
        targetCount: 1,
        successCount: 0,
        failCount: 1,
        readCount: 0,
        status: 'FAILED',
        sentAt: '2024-01-12 18:00:00',
        createdAt: '2024-01-12 17:59:00',
      },
    ],
    []
  );

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, pageSize, query]);

  const fetchData = async () => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      setData(mockData);
      setTotal(mockData.length);
      return;
    }

    setLoading(true);
    try {
      const res = await messageApi.listRecords({ page, size: pageSize, ...query });
      const payload = (res.data?.data || res.data) as PageResult<PushRecordDTO>;
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('listRecords failed, fallback mock:', error);
      const keyword = query.keyword?.trim();
      const filtered = mockData.filter((r) => {
        if (query.bizType && r.bizType !== query.bizType) return false;
        if (query.status && r.status !== query.status) return false;
        if (keyword && !r.title.includes(keyword)) return false;
        return true;
      });
      setData(filtered);
      setTotal(filtered.length);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    const values = await form.validateFields();
    setPage(1);
    setQuery({
      bizType: values.bizType || undefined,
      status: values.status || undefined,
      keyword: values.keyword?.trim() || undefined,
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const getStatusTag = (status: PushStatus) => {
    const map: Record<PushStatus, { color: string; label: string }> = {
      SENDING: { color: 'blue', label: '发送中' },
      SUCCESS: { color: 'green', label: '成功' },
      FAILED: { color: 'red', label: '失败' },
      PARTIAL: { color: 'orange', label: '部分成功' },
    };
    const cfg = map[status];
    return <Tag color={cfg.color}>{cfg.label}</Tag>;
  };

  const summary = useMemo(() => {
    const target = data.reduce((s, r) => s + r.targetCount, 0);
    const success = data.reduce((s, r) => s + r.successCount, 0);
    const read = data.reduce((s, r) => s + r.readCount, 0);
    const deliveredRate = target ? (success / target) * 100 : 0;
    const readRate = success ? (read / success) * 100 : 0;
    return { target, success, read, deliveredRate, readRate };
  }, [data]);

  const columns = [
    { title: '记录ID', dataIndex: 'id', key: 'id', width: 120 },
    {
      title: '类型',
      dataIndex: 'bizType',
      key: 'bizType',
      width: 120,
      render: (v: string) => (v === 'NOTICE' ? '站内信' : '公告'),
    },
    { title: '渠道', dataIndex: 'channel', key: 'channel', width: 120 },
    { title: '标题', dataIndex: 'title', key: 'title', width: 260, ellipsis: true },
    { title: '目标数', dataIndex: 'targetCount', key: 'targetCount', width: 110 },
    { title: '成功', dataIndex: 'successCount', key: 'successCount', width: 100 },
    { title: '失败', dataIndex: 'failCount', key: 'failCount', width: 100 },
    { title: '已读', dataIndex: 'readCount', key: 'readCount', width: 100 },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (v: PushStatus) => getStatusTag(v),
    },
    {
      title: '发送时间',
      dataIndex: 'sentAt',
      key: 'sentAt',
      width: 180,
      render: (v: string | undefined) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (v: string) => dayjs(v).format('YYYY-MM-DD HH:mm:ss'),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '消息推送' }, { title: '推送记录' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          推送记录
        </Title>
      </div>

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="目标用户数" value={summary.target} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="送达率" value={summary.deliveredRate} precision={2} suffix="%" />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="已读率" value={summary.readRate} precision={2} suffix="%" />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="已读数" value={summary.read} />
          </Card>
        </Col>
      </Row>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="bizType" label="类型">
              <Select placeholder="全部" allowClear style={{ width: 140 }}>
                <Select.Option value="NOTICE">站内信</Select.Option>
                <Select.Option value="ANNOUNCEMENT">公告</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="status" label="状态">
              <Select placeholder="全部" allowClear style={{ width: 160 }}>
                <Select.Option value="SENDING">发送中</Select.Option>
                <Select.Option value="SUCCESS">成功</Select.Option>
                <Select.Option value="FAILED">失败</Select.Option>
                <Select.Option value="PARTIAL">部分成功</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="标题" allowClear style={{ width: 220 }} />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
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
    </div>
  );
};

export default MessageRecords;
