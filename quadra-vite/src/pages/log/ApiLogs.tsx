import { useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, Form, Input, Select, Space, Table, Tag, Typography } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { logApi } from '@/services/api';
import type { ApiStatDTO, LogQueryParams, PageResult } from '@/services/types';

const { Title } = Typography;

const ApiLogs: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<ApiStatDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<LogQueryParams & { method?: string; keyword?: string }>({});

  const mockData: ApiStatDTO[] = useMemo(
    () => [
      {
        id: 'api_1',
        method: 'GET',
        path: '/v1/system/users',
        count: 1234,
        avgTime: 120,
        p95Time: 350,
        errorRate: 0.01,
        lastCalledAt: '2024-01-15 12:00:00',
      },
      {
        id: 'api_2',
        method: 'POST',
        path: '/v1/system/admin/login',
        count: 520,
        avgTime: 80,
        p95Time: 160,
        errorRate: 0.12,
        lastCalledAt: '2024-01-15 12:02:00',
      },
      {
        id: 'api_3',
        method: 'GET',
        path: '/v1/content/timeline',
        count: 884,
        avgTime: 980,
        p95Time: 1800,
        errorRate: 0.03,
        lastCalledAt: '2024-01-15 12:01:20',
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
      const res = await logApi.getApiStats({
        page,
        size: pageSize,
        keyword: query.keyword,
      });
      const payload = (res.data?.data || res.data) as PageResult<ApiStatDTO>;
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('getApiStats failed, fallback mock:', error);
      const keyword = query.keyword?.trim();
      const filtered = mockData.filter((r) => {
        if (query.method && r.method !== query.method) return false;
        if (keyword && !r.path.includes(keyword)) return false;
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
      method: values.method || undefined,
      keyword: values.keyword?.trim() || undefined,
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const getMethodTag = (method: string) => {
    const colorMap: Record<string, string> = {
      GET: 'blue',
      POST: 'green',
      PUT: 'orange',
      DELETE: 'red',
    };
    return <Tag color={colorMap[method] || 'default'}>{method}</Tag>;
  };

  const columns = [
    { title: '方法', dataIndex: 'method', key: 'method', width: 90, render: (v: string) => getMethodTag(v) },
    { title: 'Path', dataIndex: 'path', key: 'path', width: 320, ellipsis: true },
    { title: '调用次数', dataIndex: 'count', key: 'count', width: 110 },
    { title: '平均耗时(ms)', dataIndex: 'avgTime', key: 'avgTime', width: 130, render: (v: number) => <span style={{ color: v > 1000 ? '#cf1322' : undefined }}>{v}</span> },
    { title: 'P95(ms)', dataIndex: 'p95Time', key: 'p95Time', width: 110, render: (v: number | undefined) => (v ? v : '-') },
    {
      title: '错误率',
      dataIndex: 'errorRate',
      key: 'errorRate',
      width: 110,
      render: (v: number) => <span style={{ color: v > 0.1 ? '#cf1322' : undefined }}>{(v * 100).toFixed(2)}%</span>,
    },
    {
      title: '最近调用',
      dataIndex: 'lastCalledAt',
      key: 'lastCalledAt',
      width: 180,
      render: (v: string) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '日志监控' }, { title: '接口日志' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          接口日志
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="method" label="方法">
              <Select placeholder="全部" allowClear style={{ width: 160 }}>
                <Select.Option value="GET">GET</Select.Option>
                <Select.Option value="POST">POST</Select.Option>
                <Select.Option value="PUT">PUT</Select.Option>
                <Select.Option value="DELETE">DELETE</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="Path 关键字" allowClear style={{ width: 240 }} />
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
    </div>
  );
};

export default ApiLogs;
