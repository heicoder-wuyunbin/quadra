import { useCallback, useEffect, useState } from 'react';
import { Breadcrumb, Button, Card, Descriptions, Modal, Space, Table, Tag, Typography } from 'antd';
import { ReloadOutlined, EyeOutlined } from '@ant-design/icons';
import { monitorApi } from '@/services/api';

const { Title } = Typography;

interface ServiceHealth {
  name: string;
  service: string;
  status: 'UP' | 'DOWN' | 'DEGRADED';
  version: string;
  uptimeSec: number;
  cpuPercent: number;
  memPercent: number;
  qps: number;
  avgRtMs: number;
  lastCheckAt: string;
  endpoints?: {
    health?: string;
    metrics?: string;
  };
}

const Services: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [current, setCurrent] = useState<ServiceHealth | null>(null);

  const [data, setData] = useState<ServiceHealth[]>([]);

  const getStatusTag = (s: ServiceHealth['status']) => {
    const map: Record<ServiceHealth['status'], { color: string; text: string }> = {
      UP: { color: 'green', text: '健康' },
      DEGRADED: { color: 'orange', text: '降级' },
      DOWN: { color: 'red', text: '不可用' },
    };
    const cfg = map[s];
    return <Tag color={cfg.color}>{cfg.text}</Tag>;
  };

  const formatUptime = (sec: number) => {
    if (!sec) return '-';
    const d = Math.floor(sec / 86400);
    const h = Math.floor((sec % 86400) / 3600);
    const m = Math.floor((sec % 3600) / 60);
    return `${d}天${h}小时${m}分`;
  };

  const refresh = useCallback(async () => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) return;
    setLoading(true);
    try {
      const list = await monitorApi.listServices();
      setData(list as ServiceHealth[]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const columns = [
    {
      title: '服务',
      dataIndex: 'name',
      key: 'name',
      width: 180,
    },
    {
      title: '标识',
      dataIndex: 'service',
      key: 'service',
      width: 180,
      render: (v: string) => <Tag>{v}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (v: ServiceHealth['status']) => getStatusTag(v),
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 120,
    },
    {
      title: 'CPU',
      dataIndex: 'cpuPercent',
      key: 'cpuPercent',
      width: 110,
      render: (v: number) => <span style={{ color: v > 80 ? '#cf1322' : undefined }}>{v}%</span>,
    },
    {
      title: '内存',
      dataIndex: 'memPercent',
      key: 'memPercent',
      width: 110,
      render: (v: number) => <span style={{ color: v > 80 ? '#cf1322' : undefined }}>{v}%</span>,
    },
    {
      title: 'QPS',
      dataIndex: 'qps',
      key: 'qps',
      width: 110,
    },
    {
      title: '平均RT(ms)',
      dataIndex: 'avgRtMs',
      key: 'avgRtMs',
      width: 130,
      render: (v: number) => <span style={{ color: v > 1000 ? '#cf1322' : undefined }}>{v}</span>,
    },
    {
      title: '运行时长',
      dataIndex: 'uptimeSec',
      key: 'uptimeSec',
      width: 160,
      render: (v: number) => formatUptime(v),
    },
    {
      title: '最近检测',
      dataIndex: 'lastCheckAt',
      key: 'lastCheckAt',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: unknown, record: ServiceHealth) => (
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
      <Breadcrumb items={[{ title: '运维监控' }, { title: '服务监控' }]} />

      <div style={{ marginBottom: 16 }}>
        <Space align="center">
          <Title level={4} style={{ margin: 0 }}>
            服务监控
          </Title>
        </Space>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Space>
            <Button icon={<ReloadOutlined />} loading={loading} onClick={refresh}>
              刷新
            </Button>
          </Space>
        </div>

        <Table
          rowKey="service"
          columns={columns}
          dataSource={data}
          pagination={false}
          scroll={{ x: 'max-content' }}
        />
      </Card>

      <Modal title="服务详情" open={detailOpen} onCancel={() => setDetailOpen(false)} footer={null} width={860}>
        {current && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="服务">{current.name}</Descriptions.Item>
            <Descriptions.Item label="标识">{current.service}</Descriptions.Item>
            <Descriptions.Item label="状态">{getStatusTag(current.status)}</Descriptions.Item>
            <Descriptions.Item label="版本">{current.version}</Descriptions.Item>
            <Descriptions.Item label="运行时长">{formatUptime(current.uptimeSec)}</Descriptions.Item>
            <Descriptions.Item label="CPU">{current.cpuPercent}%</Descriptions.Item>
            <Descriptions.Item label="内存">{current.memPercent}%</Descriptions.Item>
            <Descriptions.Item label="QPS">{current.qps}</Descriptions.Item>
            <Descriptions.Item label="平均RT(ms)">{current.avgRtMs}</Descriptions.Item>
            <Descriptions.Item label="最近检测">{current.lastCheckAt}</Descriptions.Item>
            <Descriptions.Item label="健康检查接口">{current.endpoints?.health || '-'}</Descriptions.Item>
            <Descriptions.Item label="指标接口">{current.endpoints?.metrics || '-'}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Services;
