import { useEffect, useState } from 'react';
import { Breadcrumb, Button, Card, DatePicker, Descriptions, Form, Input, Modal, Select, Space, Table, Tag, Typography, message } from 'antd';
import { EyeOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { logApi } from '@/services/api';
import type { LoginLogDTO, LogQueryParams } from '@/services/types';

const { Title } = Typography;
const { RangePicker } = DatePicker;

const LoginLogs: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<LoginLogDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<LogQueryParams & { adminName?: string; status?: 'SUCCESS' | 'FAILED' }>({});
  const [detailOpen, setDetailOpen] = useState(false);
  const [current, setCurrent] = useState<LoginLogDTO | null>(null);

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
      const payload = await logApi.getLoginLogs({
        page,
        size: pageSize,
        startTime: query.startTime,
        endTime: query.endTime,
        keyword: query.keyword,
      });
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('getLoginLogs failed:', error);
      message.error((error as Error)?.message || '获取登录日志失败');
      setData([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    const values = await form.validateFields();
    const range = values.range as [dayjs.Dayjs, dayjs.Dayjs] | undefined;
    setPage(1);
    setQuery({
      adminName: values.adminName?.trim() || undefined,
      status: values.status || undefined,
      startTime: range?.[0]?.format('YYYY-MM-DD HH:mm:ss'),
      endTime: range?.[1]?.format('YYYY-MM-DD HH:mm:ss'),
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 140 },
    { title: '管理员', dataIndex: 'adminName', key: 'adminName', width: 140 },
    { title: 'IP', dataIndex: 'ip', key: 'ip', width: 140 },
    { title: '地点', dataIndex: 'location', key: 'location', width: 140, render: (v: string) => v || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (v: 'SUCCESS' | 'FAILED') => <Tag color={v === 'SUCCESS' ? 'green' : 'red'}>{v === 'SUCCESS' ? '成功' : '失败'}</Tag>,
    },
    { title: '失败原因', dataIndex: 'reason', key: 'reason', width: 180, render: (v: string) => v || '-' },
    {
      title: '时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (v: string) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: unknown, record: LoginLogDTO) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => {
            setCurrent(record);
            setDetailOpen(true);
          }}
        >
          详情
        </Button>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '日志监控' }, { title: '登录日志' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          登录日志
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="adminName" label="管理员">
              <Input placeholder="管理员姓名" allowClear style={{ width: 160 }} />
            </Form.Item>
            <Form.Item name="status" label="状态">
              <Select placeholder="全部" allowClear style={{ width: 160 }}>
                <Select.Option value="SUCCESS">成功</Select.Option>
                <Select.Option value="FAILED">失败</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="range" label="时间范围">
              <RangePicker showTime />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
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

      <Modal
        title="登录详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={820}
      >
        {current && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="ID">{current.id}</Descriptions.Item>
            <Descriptions.Item label="管理员">
              {current.adminName}（ID: {current.adminId}）
            </Descriptions.Item>
            <Descriptions.Item label="IP">{current.ip}</Descriptions.Item>
            <Descriptions.Item label="地点">{current.location || '-'}</Descriptions.Item>
            <Descriptions.Item label="状态">{current.status}</Descriptions.Item>
            <Descriptions.Item label="失败原因">{current.reason || '-'}</Descriptions.Item>
            <Descriptions.Item label="UserAgent">{current.userAgent}</Descriptions.Item>
            <Descriptions.Item label="时间">{dayjs(current.createdAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default LoginLogs;
