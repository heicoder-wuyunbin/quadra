import { useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, DatePicker, Descriptions, Form, Input, Modal, Space, Table, Tag, Typography } from 'antd';
import { EyeOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { logApi } from '@/services/api';
import type { LogQueryParams, PageResult, SlowSqlDTO } from '@/services/types';

const { Title } = Typography;
const { RangePicker } = DatePicker;

const SlowSQL: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<SlowSqlDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<LogQueryParams & { db?: string }>({});
  const [detailOpen, setDetailOpen] = useState(false);
  const [current, setCurrent] = useState<SlowSqlDTO | null>(null);

  const mockData: SlowSqlDTO[] = useMemo(
    () => [
      {
        id: 'sql_10001',
        db: 'quadra_user',
        sql: 'SELECT * FROM user_profile WHERE city = ? ORDER BY created_at DESC LIMIT 50;',
        executeTime: 1860,
        rowsExamined: 120000,
        explain: 'type=ALL; key=NULL; rows=120000; Extra=Using where; Using filesort',
        suggestion: '为 city + created_at 建联合索引；避免 SELECT *。',
        createdAt: '2024-01-15 11:10:00',
      },
      {
        id: 'sql_10002',
        db: 'quadra_content',
        sql: 'SELECT count(*) FROM movement WHERE state = 1;',
        executeTime: 2400,
        rowsExamined: 800000,
        explain: 'type=ALL; rows=800000',
        suggestion: '为 state 建索引或使用分区表。',
        createdAt: '2024-01-15 11:40:00',
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
      const res = await logApi.getSlowSql({
        page,
        size: pageSize,
        startTime: query.startTime,
        endTime: query.endTime,
        keyword: query.keyword,
      });
      const payload = (res.data?.data || res.data) as PageResult<SlowSqlDTO>;
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('getSlowSql failed, fallback mock:', error);
      const keyword = query.keyword?.trim();
      const filtered = mockData.filter((r) => {
        if (query.db && r.db !== query.db) return false;
        if (keyword && !r.sql.includes(keyword)) return false;
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
      db: values.db?.trim() || undefined,
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

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 140 },
    { title: '库', dataIndex: 'db', key: 'db', width: 160, render: (v: string | undefined) => v || '-' },
    { title: 'SQL', dataIndex: 'sql', key: 'sql', width: 520, ellipsis: true },
    {
      title: '耗时(ms)',
      dataIndex: 'executeTime',
      key: 'executeTime',
      width: 120,
      render: (v: number) => <Tag color={v > 2000 ? 'red' : 'orange'}>{v}</Tag>,
    },
    { title: '扫描行数', dataIndex: 'rowsExamined', key: 'rowsExamined', width: 120, render: (v: number | undefined) => (v ? v : '-') },
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
      render: (_: unknown, record: SlowSqlDTO) => (
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
      <Breadcrumb items={[{ title: '日志监控' }, { title: '慢查询日志' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          慢查询日志
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="db" label="数据库">
              <Input placeholder="例如：quadra_user" allowClear style={{ width: 200 }} />
            </Form.Item>
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="SQL 关键字" allowClear style={{ width: 240 }} />
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

      <Modal title="慢 SQL 详情" open={detailOpen} onCancel={() => setDetailOpen(false)} footer={null} width={960}>
        {current && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="ID">{current.id}</Descriptions.Item>
            <Descriptions.Item label="数据库">{current.db || '-'}</Descriptions.Item>
            <Descriptions.Item label="耗时(ms)">{current.executeTime}</Descriptions.Item>
            <Descriptions.Item label="扫描行数">{current.rowsExamined || '-'}</Descriptions.Item>
            <Descriptions.Item label="SQL">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.sql}</pre>
            </Descriptions.Item>
            <Descriptions.Item label="Explain">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.explain || '-'}</pre>
            </Descriptions.Item>
            <Descriptions.Item label="优化建议">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>{current.suggestion || '-'}</pre>
            </Descriptions.Item>
            <Descriptions.Item label="时间">{dayjs(current.createdAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default SlowSQL;
