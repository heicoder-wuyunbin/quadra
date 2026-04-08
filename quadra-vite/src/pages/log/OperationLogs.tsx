import { useEffect, useState } from 'react';
import { Breadcrumb, Button, Card, DatePicker, Descriptions, Form, Input, message, Modal, Space, Table, Tag, Typography } from 'antd';
import { DownloadOutlined, EyeOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { logApi } from '@/services/api';
import type { LogQueryParams, OperationLogDTO } from '@/services/types';

const { Title } = Typography;
const { RangePicker } = DatePicker;

const OperationLogs: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OperationLogDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<LogQueryParams & { adminName?: string; module?: string; action?: string }>({});
  const [detailOpen, setDetailOpen] = useState(false);
  const [current, setCurrent] = useState<OperationLogDTO | null>(null);

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
      const payload = await logApi.getOperationLogs({
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
      console.warn('getOperationLogs failed:', error);
      message.error((error as Error)?.message || '获取操作日志失败');
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
      module: values.module?.trim() || undefined,
      action: values.action?.trim() || undefined,
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

  const isDangerAction = (action: string) => {
    const dangerKeywords = ['删除', '禁用', '封禁', '批量删除', 'DROP', 'DELETE'];
    return dangerKeywords.some((k) => action.toUpperCase().includes(k.toUpperCase()));
  };

  const exportCsv = () => {
    if (!data.length) {
      message.warning('暂无可导出数据');
      return;
    }
    const header = ['id', 'adminName', 'module', 'action', 'responseStatus', 'executeTime', 'ip', 'createdAt'];
    const rows = data.map((r) => [
      r.id,
      r.adminName,
      r.module,
      r.action,
      String(r.responseStatus),
      String(r.executeTime),
      r.ip,
      r.createdAt,
    ]);
    const csv = [header, ...rows]
      .map((row) =>
        row
          .map((cell) => `"${String(cell).replaceAll('"', '""')}"`)
          .join(',')
      )
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `operation-logs-${dayjs().format('YYYYMMDD-HHmmss')}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    message.success('已导出 CSV');
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 140 },
    { title: '管理员', dataIndex: 'adminName', key: 'adminName', width: 140 },
    { title: '模块', dataIndex: 'module', key: 'module', width: 140 },
    {
      title: '动作',
      dataIndex: 'action',
      key: 'action',
      width: 180,
      render: (v: string) => (isDangerAction(v) ? <Tag color="red">{v}</Tag> : v),
    },
    {
      title: '状态码',
      dataIndex: 'responseStatus',
      key: 'responseStatus',
      width: 110,
      render: (v: number) => <Tag color={v >= 200 && v < 300 ? 'green' : v >= 400 ? 'red' : 'orange'}>{v}</Tag>,
    },
    {
      title: '耗时(ms)',
      dataIndex: 'executeTime',
      key: 'executeTime',
      width: 110,
      render: (v: number) => <span style={{ color: v > 1000 ? '#cf1322' : undefined }}>{v}</span>,
    },
    { title: 'IP', dataIndex: 'ip', key: 'ip', width: 140 },
    {
      title: '时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (v: string) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'actionBtn',
      width: 120,
      fixed: 'right' as const,
      render: (_: unknown, record: OperationLogDTO) => (
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
      <Breadcrumb items={[{ title: '日志监控' }, { title: '操作日志' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          操作日志
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="adminName" label="管理员">
              <Input placeholder="管理员姓名" allowClear style={{ width: 160 }} />
            </Form.Item>
            <Form.Item name="module" label="模块">
              <Input placeholder="例如：用户管理" allowClear style={{ width: 160 }} />
            </Form.Item>
            <Form.Item name="action" label="动作">
              <Input placeholder="例如：删除/禁用" allowClear style={{ width: 160 }} />
            </Form.Item>
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="任意字段" allowClear style={{ width: 160 }} />
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
                <Button icon={<DownloadOutlined />} onClick={exportCsv}>
                  导出CSV
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
        title="操作详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={860}
      >
        {current && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="ID">{current.id}</Descriptions.Item>
            <Descriptions.Item label="管理员">
              {current.adminName}（ID: {current.adminId}）
            </Descriptions.Item>
            <Descriptions.Item label="模块">{current.module}</Descriptions.Item>
            <Descriptions.Item label="动作">{current.action}</Descriptions.Item>
            {current.targetId !== undefined && (
              <Descriptions.Item label="目标">
                {current.targetName || '-'}（{current.targetId}）
              </Descriptions.Item>
            )}
            <Descriptions.Item label="状态码">{current.responseStatus}</Descriptions.Item>
            <Descriptions.Item label="耗时(ms)">{current.executeTime}</Descriptions.Item>
            <Descriptions.Item label="IP">{current.ip}</Descriptions.Item>
            <Descriptions.Item label="UserAgent">{current.userAgent}</Descriptions.Item>
            <Descriptions.Item label="请求参数">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>
                {JSON.stringify(current.requestParams, null, 2)}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="时间">
              {dayjs(current.createdAt).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default OperationLogs;
