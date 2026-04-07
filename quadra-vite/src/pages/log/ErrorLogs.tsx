import { useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, DatePicker, Descriptions, Form, Input, message, Modal, Select, Space, Table, Tag, Typography } from 'antd';
import { CheckOutlined, EyeOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { logApi } from '@/services/api';
import type { ErrorLogDTO, LogQueryParams, PageResult } from '@/services/types';

const { Title } = Typography;
const { RangePicker } = DatePicker;

const ErrorLogs: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<ErrorLogDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<LogQueryParams & { level?: string; service?: string; handled?: boolean }>({});
  const [detailOpen, setDetailOpen] = useState(false);
  const [current, setCurrent] = useState<ErrorLogDTO | null>(null);

  const mockData: ErrorLogDTO[] = useMemo(
    () => [
      {
        id: 'err_10001',
        level: 'ERROR',
        service: 'quadra-system',
        message: 'NullPointerException: user is null',
        stackTrace: 'java.lang.NullPointerException\n  at com.xxx.UserService.get(UserService.java:42)\n  ...',
        requestId: 'req_abc',
        url: '/v1/system/users/10001',
        params: { id: 10001 },
        handled: false,
        createdAt: '2024-01-15 11:30:00',
      },
      {
        id: 'err_10002',
        level: 'WARN',
        service: 'quadra-content',
        message: 'Slow response > 2000ms',
        stackTrace: '',
        requestId: 'req_def',
        url: '/v1/content/timeline',
        params: { pageNo: 1, pageSize: 20 },
        handled: true,
        handledBy: '系统管理员',
        handledAt: '2024-01-15 12:00:00',
        createdAt: '2024-01-15 11:50:00',
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
      const res = await logApi.getErrorLogs({
        page,
        size: pageSize,
        startTime: query.startTime,
        endTime: query.endTime,
        keyword: query.keyword,
        level: query.level,
        service: query.service,
        handled: query.handled,
      });
      const payload = (res.data?.data || res.data) as PageResult<ErrorLogDTO>;
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('getErrorLogs failed, fallback mock:', error);
      const keyword = query.keyword?.trim();
      const filtered = mockData.filter((r) => {
        if (query.level && r.level !== query.level) return false;
        if (query.service && !r.service.includes(query.service)) return false;
        if (query.handled !== undefined && !!r.handled !== query.handled) return false;
        if (keyword && !(r.message.includes(keyword) || r.url.includes(keyword) || r.requestId.includes(keyword))) return false;
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
    const range = values.range as [dayjs.Dayjs, dayjs.Dayjs] | undefined;
    setPage(1);
    setQuery({
      level: values.level || undefined,
      service: values.service?.trim() || undefined,
      handled: values.handled,
      keyword: values.keyword?.trim() || undefined,
      startTime: range?.[0]?.format('YYYY-MM-DD HH:mm:ss'),
      endTime: range?.[1]?.format('YYYY-MM-DD HH:mm:ss'),
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const getLevelTag = (level: 'ERROR' | 'WARN' | 'INFO') => {
    const map = {
      ERROR: { color: 'red', label: 'ERROR' },
      WARN: { color: 'orange', label: 'WARN' },
      INFO: { color: 'blue', label: 'INFO' },
    } as const;
    const cfg = map[level];
    return <Tag color={cfg.color}>{cfg.label}</Tag>;
  };

  const toggleHandled = async (record: ErrorLogDTO) => {
    try {
      await logApi.markErrorHandled(record.id, !record.handled);
      message.success(record.handled ? '已取消处理标记' : '已标记为已处理');
      fetchData();
    } catch (error) {
      console.warn('markErrorHandled failed (mock ok):', error);
      message.success(record.handled ? '（模拟）已取消处理标记' : '（模拟）已标记为已处理');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 140 },
    { title: '级别', dataIndex: 'level', key: 'level', width: 110, render: (v: ErrorLogDTO['level']) => getLevelTag(v) },
    { title: '服务', dataIndex: 'service', key: 'service', width: 160 },
    { title: '消息', dataIndex: 'message', key: 'message', width: 320, ellipsis: true },
    { title: 'RequestId', dataIndex: 'requestId', key: 'requestId', width: 160, ellipsis: true },
    { title: 'URL', dataIndex: 'url', key: 'url', width: 260, ellipsis: true },
    {
      title: '处理状态',
      dataIndex: 'handled',
      key: 'handled',
      width: 120,
      render: (v: boolean | undefined) => <Tag color={v ? 'green' : 'default'}>{v ? '已处理' : '未处理'}</Tag>,
    },
    {
      title: '时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (v: string) => dayjs(v).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right' as const,
      render: (_: unknown, record: ErrorLogDTO) => (
        <Space>
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
          <Button type="link" icon={<CheckOutlined />} onClick={() => toggleHandled(record)}>
            {record.handled ? '取消已处理' : '标记已处理'}
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '日志监控' }, { title: '错误日志' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          错误日志
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline" initialValues={{ handled: undefined }}>
            <Form.Item name="service" label="服务">
              <Input placeholder="例如：quadra-system" allowClear style={{ width: 200 }} />
            </Form.Item>
            <Form.Item name="level" label="级别">
              <Select placeholder="全部" allowClear style={{ width: 150 }}>
                <Select.Option value="ERROR">ERROR</Select.Option>
                <Select.Option value="WARN">WARN</Select.Option>
                <Select.Option value="INFO">INFO</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="handled" label="已处理">
              <Select placeholder="全部" allowClear style={{ width: 150 }}>
                <Select.Option value={true}>是</Select.Option>
                <Select.Option value={false}>否</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="message/url/requestId" allowClear style={{ width: 220 }} />
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

      <Modal title="错误详情" open={detailOpen} onCancel={() => setDetailOpen(false)} footer={null} width={900}>
        {current && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="ID">{current.id}</Descriptions.Item>
            <Descriptions.Item label="级别">{getLevelTag(current.level)}</Descriptions.Item>
            <Descriptions.Item label="服务">{current.service}</Descriptions.Item>
            <Descriptions.Item label="消息">{current.message}</Descriptions.Item>
            <Descriptions.Item label="RequestId">{current.requestId}</Descriptions.Item>
            <Descriptions.Item label="URL">{current.url}</Descriptions.Item>
            <Descriptions.Item label="参数">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{JSON.stringify(current.params, null, 2)}</pre>
            </Descriptions.Item>
            <Descriptions.Item label="堆栈">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.stackTrace || '-'}</pre>
            </Descriptions.Item>
            <Descriptions.Item label="处理状态">{current.handled ? '已处理' : '未处理'}</Descriptions.Item>
            <Descriptions.Item label="处理人">{current.handledBy || '-'}</Descriptions.Item>
            <Descriptions.Item label="处理时间">{current.handledAt ? dayjs(current.handledAt).format('YYYY-MM-DD HH:mm:ss') : '-'}</Descriptions.Item>
            <Descriptions.Item label="时间">{dayjs(current.createdAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default ErrorLogs;
