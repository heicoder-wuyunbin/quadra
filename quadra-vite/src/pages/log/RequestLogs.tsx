import { useEffect, useState } from 'react';
import { Breadcrumb, Button, Card, DatePicker, Descriptions, Form, Input, Modal, Select, Space, Table, Tag, Typography, message } from 'antd';
import { SearchOutlined, EyeOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { logApi } from '@/services/api';
import type { RequestLogDTO, RequestLogQueryParams } from '@/services/types';

const { Title } = Typography;
const { RangePicker } = DatePicker;

const methodColors: Record<string, string> = {
  GET: 'blue',
  POST: 'green',
  PUT: 'orange',
  DELETE: 'red',
};

const RequestLogs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<RequestLogDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<RequestLogQueryParams>({});
  const [detailOpen, setDetailOpen] = useState(false);
  const [current, setCurrent] = useState<RequestLogDTO | null>(null);

  const fetchData = async () => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      return;
    }

    setLoading(true);
    try {
      const payload = await logApi.getRequestLogs({
        page,
        size: pageSize,
        ...query,
      });
      setData(payload.records || payload.list || []);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn(error);
      message.error((error as Error)?.message || '获取访问日志失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, pageSize, query]);

  const openDetail = (record: RequestLogDTO) => {
    setCurrent(record);
    setDetailOpen(true);
  };

  const columns: ColumnsType<RequestLogDTO> = [
    {
      title: '时间',
      dataIndex: 'createdAt',
      width: 180,
    },
    {
      title: '服务',
      dataIndex: 'service',
      width: 140,
      render: (v: string) => <Tag>{v}</Tag>,
    },
    {
      title: '方法',
      dataIndex: 'method',
      width: 90,
      render: (v: string) => <Tag color={methodColors[v] || 'default'}>{v}</Tag>,
    },
    {
      title: '路径',
      dataIndex: 'path',
      ellipsis: true,
    },
    {
      title: '状态码',
      dataIndex: 'statusCode',
      width: 90,
      render: (v: number) => <Tag color={v >= 500 ? 'red' : v >= 400 ? 'orange' : 'green'}>{v}</Tag>,
    },
    {
      title: '耗时(ms)',
      dataIndex: 'durationMs',
      width: 100,
      sorter: (a, b) => a.durationMs - b.durationMs,
    },
    {
      title: 'TraceId',
      dataIndex: 'traceId',
      width: 170,
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 90,
      fixed: 'right',
      render: (_: unknown, record: RequestLogDTO) => (
        <Button type="link" icon={<EyeOutlined />} onClick={() => openDetail(record)}>
          详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Breadcrumb style={{ marginBottom: 16 }}>
        <Breadcrumb.Item>日志监控</Breadcrumb.Item>
        <Breadcrumb.Item>访问日志</Breadcrumb.Item>
      </Breadcrumb>

      <Card>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }}>
          <Title level={4} style={{ margin: 0 }}>
            访问日志
          </Title>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData()}>
            刷新
          </Button>
        </Space>

        <Form
          layout="inline"
          style={{ marginBottom: 16 }}
          onFinish={(values) => {
            const range = values.range as [dayjs.Dayjs, dayjs.Dayjs] | undefined;
            setPage(1);
            setQuery({
              service: values.service || undefined,
              method: values.method || undefined,
              statusCode: values.statusCode ? Number(values.statusCode) : undefined,
              pathKeyword: values.pathKeyword?.trim() || undefined,
              traceId: values.traceId?.trim() || undefined,
              adminId: values.adminId ? Number(values.adminId) : undefined,
              startTime: range ? range[0].format('YYYY-MM-DD HH:mm:ss') : undefined,
              endTime: range ? range[1].format('YYYY-MM-DD HH:mm:ss') : undefined,
            });
          }}
        >
          <Form.Item name="service" label="服务">
            <Input placeholder="如 quadra-system" style={{ width: 170 }} allowClear />
          </Form.Item>
          <Form.Item name="method" label="方法">
            <Select
              placeholder="全部"
              style={{ width: 120 }}
              allowClear
              options={['GET', 'POST', 'PUT', 'DELETE'].map((m) => ({ value: m, label: m }))}
            />
          </Form.Item>
          <Form.Item name="statusCode" label="状态码">
            <Input placeholder="401/404/500" style={{ width: 120 }} allowClear />
          </Form.Item>
          <Form.Item name="pathKeyword" label="路径">
            <Input placeholder="关键字" style={{ width: 180 }} allowClear />
          </Form.Item>
          <Form.Item name="traceId" label="TraceId">
            <Input placeholder="requestId/traceId" style={{ width: 190 }} allowClear />
          </Form.Item>
          <Form.Item name="adminId" label="管理员ID">
            <Input placeholder="如 1" style={{ width: 120 }} allowClear />
          </Form.Item>
          <Form.Item name="range" label="时间">
            <RangePicker showTime style={{ width: 360 }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" icon={<SearchOutlined />} htmlType="submit">
              查询
            </Button>
          </Form.Item>
          <Form.Item>
            <Button
              onClick={() => {
                setPage(1);
                setQuery({});
              }}
            >
              重置
            </Button>
          </Form.Item>
        </Form>

        <Table
          rowKey="id"
          columns={columns}
          dataSource={data}
          loading={loading}
          scroll={{ x: 1100 }}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            onChange: (p, ps) => {
              setPage(p);
              setPageSize(ps);
            },
          }}
        />
      </Card>

      <Modal
        open={detailOpen}
        title="访问日志详情"
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={900}
      >
        {current && (
          <>
            <Descriptions column={2} size="small" bordered style={{ marginBottom: 12 }}>
              <Descriptions.Item label="时间">{current.createdAt}</Descriptions.Item>
              <Descriptions.Item label="服务">{current.service}</Descriptions.Item>
              <Descriptions.Item label="方法">{current.method}</Descriptions.Item>
              <Descriptions.Item label="状态码">{current.statusCode}</Descriptions.Item>
              <Descriptions.Item label="耗时(ms)">{current.durationMs}</Descriptions.Item>
              <Descriptions.Item label="管理员ID">{current.adminId ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="路径" span={2}>
                {current.path}
              </Descriptions.Item>
              <Descriptions.Item label="Query" span={2}>
                {current.queryString || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="TraceId" span={2}>
                {current.traceId || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="IP">{current.ipAddress || '-'}</Descriptions.Item>
              <Descriptions.Item label="UA">{current.userAgent || '-'}</Descriptions.Item>
            </Descriptions>

            <Card size="small" title="请求头（脱敏）" style={{ marginBottom: 12 }}>
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.requestHeaders || '-'}</pre>
            </Card>
            <Card size="small" title="请求体" style={{ marginBottom: 12 }}>
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.requestBody || '-'}</pre>
            </Card>
            <Card size="small" title="响应体">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.responseBody || '-'}</pre>
            </Card>
          </>
        )}
      </Modal>
    </div>
  );
};

export default RequestLogs;

