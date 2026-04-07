import { useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, Col, DatePicker, Form, Input, Progress, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import { ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import type { Dayjs } from 'dayjs';

const { Title } = Typography;
const { RangePicker } = DatePicker;

interface PerformanceSnapshot {
  id: string;
  service: string;
  cpuPercent: number;
  memPercent: number;
  heapUsedMb: number;
  heapMaxMb: number;
  gcCount: number;
  threadCount: number;
  rps: number;
  p95Ms: number;
  errorRate: number; // 0-1
  updatedAt: string;
}

const Performance: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  // TODO：后端补齐监控接口后替换为真实 API（如 /v1/monitor/performance）
  const mockData: PerformanceSnapshot[] = useMemo(
    () => [
      {
        id: 'perf_gateway',
        service: 'quadra-gateway',
        cpuPercent: 18,
        memPercent: 42,
        heapUsedMb: 320,
        heapMaxMb: 1024,
        gcCount: 12,
        threadCount: 86,
        rps: 210,
        p95Ms: 120,
        errorRate: 0.01,
        updatedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        id: 'perf_user',
        service: 'quadra-user',
        cpuPercent: 25,
        memPercent: 55,
        heapUsedMb: 540,
        heapMaxMb: 1024,
        gcCount: 18,
        threadCount: 110,
        rps: 80,
        p95Ms: 180,
        errorRate: 0.02,
        updatedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        id: 'perf_content',
        service: 'quadra-content',
        cpuPercent: 68,
        memPercent: 78,
        heapUsedMb: 860,
        heapMaxMb: 1024,
        gcCount: 56,
        threadCount: 180,
        rps: 60,
        p95Ms: 1600,
        errorRate: 0.08,
        updatedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        id: 'perf_system',
        service: 'quadra-system',
        cpuPercent: 9,
        memPercent: 38,
        heapUsedMb: 280,
        heapMaxMb: 1024,
        gcCount: 8,
        threadCount: 70,
        rps: 40,
        p95Ms: 90,
        errorRate: 0.005,
        updatedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      },
    ],
    []
  );

  const [data, setData] = useState<PerformanceSnapshot[]>(mockData);

  const summary = useMemo(() => {
    const cpu = data.reduce((s, r) => s + r.cpuPercent, 0) / (data.length || 1);
    const mem = data.reduce((s, r) => s + r.memPercent, 0) / (data.length || 1);
    const rps = data.reduce((s, r) => s + r.rps, 0);
    const err = data.reduce((s, r) => s + r.errorRate * r.rps, 0) / (rps || 1);
    return { cpu, mem, rps, err };
  }, [data]);

  const refresh = async () => {
    setLoading(true);
    try {
      await new Promise((r) => setTimeout(r, 400));
      setData(
        data.map((item) => ({
          ...item,
          updatedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          cpuPercent: Math.max(0, Math.min(95, Math.round(item.cpuPercent + (Math.random() * 10 - 5)))),
          memPercent: Math.max(0, Math.min(95, Math.round(item.memPercent + (Math.random() * 10 - 5)))),
          rps: Math.max(0, Math.round(item.rps + (Math.random() * 10 - 5))),
          p95Ms: Math.max(0, Math.round(item.p95Ms + (Math.random() * 200 - 100))),
          errorRate: Math.max(0, Math.min(0.5, item.errorRate + (Math.random() * 0.02 - 0.01))),
        }))
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    const values = await form.validateFields();
    const keyword: string | undefined = values.keyword?.trim();
    const range = values.range as [Dayjs, Dayjs] | undefined;
    // 这里 range 只是预留：真实联调时传给后端即可；mock 仅按 keyword 过滤
    void range;
    setData(keyword ? mockData.filter((r) => r.service.includes(keyword)) : mockData);
  };

  const handleReset = () => {
    form.resetFields();
    setData(mockData);
  };

  const columns = [
    { title: '服务', dataIndex: 'service', key: 'service', width: 180, render: (v: string) => <Tag>{v}</Tag> },
    {
      title: 'CPU',
      dataIndex: 'cpuPercent',
      key: 'cpuPercent',
      width: 140,
      render: (v: number) => <Progress percent={v} size="small" status={v > 85 ? 'exception' : undefined} />,
    },
    {
      title: '内存',
      dataIndex: 'memPercent',
      key: 'memPercent',
      width: 140,
      render: (v: number) => <Progress percent={v} size="small" status={v > 85 ? 'exception' : undefined} />,
    },
    {
      title: 'JVM Heap',
      key: 'heap',
      width: 160,
      render: (_: unknown, r: PerformanceSnapshot) => `${r.heapUsedMb} / ${r.heapMaxMb} MB`,
    },
    { title: 'GC次数', dataIndex: 'gcCount', key: 'gcCount', width: 100 },
    { title: '线程数', dataIndex: 'threadCount', key: 'threadCount', width: 100 },
    { title: 'RPS', dataIndex: 'rps', key: 'rps', width: 100 },
    {
      title: 'P95(ms)',
      dataIndex: 'p95Ms',
      key: 'p95Ms',
      width: 110,
      render: (v: number) => <span style={{ color: v > 1000 ? '#cf1322' : undefined }}>{v}</span>,
    },
    {
      title: '错误率',
      dataIndex: 'errorRate',
      key: 'errorRate',
      width: 120,
      render: (v: number) => <span style={{ color: v > 0.1 ? '#cf1322' : undefined }}>{(v * 100).toFixed(2)}%</span>,
    },
    { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  ];
  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '运维监控' }, { title: '性能监控' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          性能监控
        </Title>
      </div>

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="平均 CPU" value={summary.cpu} precision={2} suffix="%" />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="平均 内存" value={summary.mem} precision={2} suffix="%" />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="总 RPS" value={summary.rps} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card variant="borderless">
            <Statistic title="加权错误率" value={summary.err * 100} precision={2} suffix="%" />
          </Card>
        </Col>
      </Row>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="keyword" label="服务关键字">
              <Input placeholder="例如：quadra-user" allowClear style={{ width: 220 }} />
            </Form.Item>
            <Form.Item name="range" label="时间范围（预留）">
              <RangePicker showTime />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
                <Button icon={<ReloadOutlined />} loading={loading} onClick={refresh}>
                  刷新
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
          pagination={false}
          scroll={{ x: 'max-content' }}
        />
      </Card>
    </div>
  );
};

export default Performance;
