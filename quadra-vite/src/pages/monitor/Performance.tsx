import { useCallback, useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, Col, DatePicker, Form, Input, Progress, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import { ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import type { Dayjs } from 'dayjs';
import { monitorApi } from '@/services/api';

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

  const [data, setData] = useState<PerformanceSnapshot[]>([]);

  const summary = useMemo(() => {
    const cpu = data.reduce((s, r) => s + r.cpuPercent, 0) / (data.length || 1);
    const mem = data.reduce((s, r) => s + r.memPercent, 0) / (data.length || 1);
    const rps = data.reduce((s, r) => s + r.rps, 0);
    const err = data.reduce((s, r) => s + r.errorRate * r.rps, 0) / (rps || 1);
    return { cpu, mem, rps, err };
  }, [data]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const fetchData = useCallback(async (keyword?: string) => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) return;
    setLoading(true);
    try {
      const list = await monitorApi.listPerformance(keyword ? { keyword } : undefined);
      setData(list as PerformanceSnapshot[]);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleSearch = async () => {
    const values = await form.validateFields();
    const keyword: string | undefined = values.keyword?.trim();
    const range = values.range as [Dayjs, Dayjs] | undefined;
    // 这里 range 只是预留：真实联调时传给后端即可；mock 仅按 keyword 过滤
    void range;
    fetchData(keyword || undefined);
  };

  const handleReset = () => {
    form.resetFields();
    fetchData();
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
