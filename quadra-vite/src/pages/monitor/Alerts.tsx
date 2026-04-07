import { useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, Descriptions, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Table, Tag, Typography } from 'antd';
import { PlusOutlined, EditOutlined, EyeOutlined, BellOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';

const { Title } = Typography;

type AlertLevel = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
type AlertStatus = 'OPEN' | 'ACKED' | 'RESOLVED';
type AlertChannel = 'IN_APP' | 'EMAIL' | 'SMS';

interface AlertRule {
  id: number;
  name: string;
  level: AlertLevel;
  metric: string;
  threshold: string; // e.g. "cpu > 80%"
  channel: AlertChannel[];
  enabled: boolean;
  description?: string;
  lastTriggeredAt?: string;
  createdAt: string;
}

interface AlertEvent {
  id: string;
  ruleId: number;
  ruleName: string;
  level: AlertLevel;
  service: string;
  message: string;
  status: AlertStatus;
  occurredAt: string;
  acknowledgedBy?: string;
  acknowledgedAt?: string;
  resolvedAt?: string;
}

const Alerts: React.FC = () => {
  const [tab, setTab] = useState<'rules' | 'events'>('events');
  const [loading, setLoading] = useState(false);
  const [ruleForm] = Form.useForm();
  const [queryForm] = Form.useForm();

  const [editOpen, setEditOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [editingRule, setEditingRule] = useState<AlertRule | null>(null);

  const [currentEvent, setCurrentEvent] = useState<AlertEvent | null>(null);
  const [eventDetailOpen, setEventDetailOpen] = useState(false);

  // TODO：后端补齐告警接口后替换为真实 API（如 /v1/monitor/alerts）
  const mockRules: AlertRule[] = useMemo(
    () => [
      {
        id: 1,
        name: 'CPU 使用率过高',
        level: 'HIGH',
        metric: 'cpu',
        threshold: 'cpu > 80%',
        channel: ['IN_APP'],
        enabled: true,
        description: '当任一服务 CPU 使用率持续高于 80% 时触发。',
        lastTriggeredAt: '2024-01-15 11:40:00',
        createdAt: '2024-01-10 10:00:00',
      },
      {
        id: 2,
        name: '接口错误率过高',
        level: 'CRITICAL',
        metric: 'errorRate',
        threshold: 'errorRate > 10%',
        channel: ['IN_APP', 'EMAIL'],
        enabled: true,
        description: '当网关统计的接口错误率过高时触发。',
        lastTriggeredAt: '2024-01-15 12:02:00',
        createdAt: '2024-01-10 10:10:00',
      },
      {
        id: 3,
        name: '慢 SQL 激增',
        level: 'MEDIUM',
        metric: 'slowSql',
        threshold: 'slowSqlCount > 100 / 5min',
        channel: ['IN_APP'],
        enabled: false,
        createdAt: '2024-01-11 10:00:00',
      },
    ],
    []
  );

  const mockEvents: AlertEvent[] = useMemo(
    () => [
      {
        id: 'evt_10001',
        ruleId: 2,
        ruleName: '接口错误率过高',
        level: 'CRITICAL',
        service: 'quadra-gateway',
        message: '过去 5 分钟错误率达到 12.3%',
        status: 'OPEN',
        occurredAt: '2024-01-15 12:02:00',
      },
      {
        id: 'evt_10002',
        ruleId: 1,
        ruleName: 'CPU 使用率过高',
        level: 'HIGH',
        service: 'quadra-content',
        message: 'CPU 峰值达到 92%',
        status: 'ACKED',
        occurredAt: '2024-01-15 11:40:00',
        acknowledgedBy: '系统管理员',
        acknowledgedAt: '2024-01-15 11:45:00',
      },
      {
        id: 'evt_10003',
        ruleId: 1,
        ruleName: 'CPU 使用率过高',
        level: 'HIGH',
        service: 'quadra-content',
        message: 'CPU 已恢复至 65%',
        status: 'RESOLVED',
        occurredAt: '2024-01-15 11:40:00',
        resolvedAt: '2024-01-15 12:10:00',
      },
    ],
    []
  );

  const [rules, setRules] = useState<AlertRule[]>(mockRules);
  const [events, setEvents] = useState<AlertEvent[]>(mockEvents);

  useEffect(() => {
    queryForm.setFieldsValue({ level: undefined, status: undefined, keyword: undefined });
  }, [queryForm]);

  const getLevelTag = (level: AlertLevel) => {
    const map: Record<AlertLevel, { color: string; text: string }> = {
      CRITICAL: { color: 'red', text: '致命' },
      HIGH: { color: 'orange', text: '高' },
      MEDIUM: { color: 'blue', text: '中' },
      LOW: { color: 'default', text: '低' },
    };
    const cfg = map[level];
    return <Tag color={cfg.color}>{cfg.text}</Tag>;
  };

  const getStatusTag = (status: AlertStatus) => {
    const map: Record<AlertStatus, { color: string; text: string }> = {
      OPEN: { color: 'red', text: '未处理' },
      ACKED: { color: 'orange', text: '已确认' },
      RESOLVED: { color: 'green', text: '已恢复' },
    };
    const cfg = map[status];
    return <Tag color={cfg.color}>{cfg.text}</Tag>;
  };

  const refresh = async () => {
    setLoading(true);
    try {
      await new Promise((r) => setTimeout(r, 400));
      // mock 下刷新一下发生时间，模拟新数据
      setEvents((prev) =>
        prev.map((e) => ({
          ...e,
          occurredAt: e.status === 'OPEN' ? dayjs().format('YYYY-MM-DD HH:mm:ss') : e.occurredAt,
        }))
      );
    } finally {
      setLoading(false);
    }
  };

  const openCreateRule = () => {
    setEditingRule(null);
    ruleForm.resetFields();
    ruleForm.setFieldsValue({ level: 'MEDIUM', channel: ['IN_APP'], enabled: true });
    setEditOpen(true);
  };

  const openEditRule = (rule: AlertRule) => {
    setEditingRule(rule);
    ruleForm.setFieldsValue(rule);
    setEditOpen(true);
  };

  const saveRule = async () => {
    const values = await ruleForm.validateFields();
    try {
      if (editingRule) {
        setRules((prev) =>
          prev.map((r) => (r.id === editingRule.id ? { ...r, ...values } : r))
        );
        message.success('（模拟）规则已更新');
      } else {
        const newRule: AlertRule = {
          id: Math.max(0, ...rules.map((r) => r.id)) + 1,
          createdAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          ...values,
        };
        setRules((prev) => [newRule, ...prev]);
        message.success('（模拟）规则已创建');
      }
      setEditOpen(false);
    } catch (error) {
      console.warn('saveRule failed:', error);
    }
  };

  const toggleRuleEnabled = async (rule: AlertRule) => {
    setRules((prev) => prev.map((r) => (r.id === rule.id ? { ...r, enabled: !r.enabled } : r)));
    message.success(rule.enabled ? '已禁用规则' : '已启用规则');
  };

  const deleteRule = async (rule: AlertRule) => {
    setRules((prev) => prev.filter((r) => r.id !== rule.id));
    message.success('已删除规则');
  };

  const ackEvent = (evt: AlertEvent) => {
    if (evt.status !== 'OPEN') return;
    setEvents((prev) =>
      prev.map((e) =>
        e.id === evt.id
          ? {
              ...e,
              status: 'ACKED',
              acknowledgedBy: '当前管理员',
              acknowledgedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
            }
          : e
      )
    );
    message.success('已确认告警');
  };

  const resolveEvent = (evt: AlertEvent) => {
    if (evt.status === 'RESOLVED') return;
    setEvents((prev) =>
      prev.map((e) => (e.id === evt.id ? { ...e, status: 'RESOLVED', resolvedAt: dayjs().format('YYYY-MM-DD HH:mm:ss') } : e))
    );
    message.success('已标记恢复');
  };

  const handleQuery = async () => {
    const values = await queryForm.validateFields();
    const keyword = values.keyword?.trim();
    if (tab === 'rules') {
      setRules(
        mockRules.filter((r) => {
          if (values.level && r.level !== values.level) return false;
          if (keyword && !(r.name.includes(keyword) || r.threshold.includes(keyword))) return false;
          return true;
        })
      );
    } else {
      setEvents(
        mockEvents.filter((e) => {
          if (values.level && e.level !== values.level) return false;
          if (values.status && e.status !== values.status) return false;
          if (keyword && !(e.ruleName.includes(keyword) || e.service.includes(keyword) || e.message.includes(keyword))) return false;
          return true;
        })
      );
    }
  };

  const resetQuery = () => {
    queryForm.resetFields();
    setRules(mockRules);
    setEvents(mockEvents);
  };

  const ruleColumns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '规则名称', dataIndex: 'name', key: 'name', width: 220 },
    { title: '级别', dataIndex: 'level', key: 'level', width: 100, render: (v: AlertLevel) => getLevelTag(v) },
    { title: '指标', dataIndex: 'metric', key: 'metric', width: 120, render: (v: string) => <Tag>{v}</Tag> },
    { title: '阈值', dataIndex: 'threshold', key: 'threshold', width: 220, ellipsis: true },
    {
      title: '渠道',
      dataIndex: 'channel',
      key: 'channel',
      width: 160,
      render: (v: AlertChannel[]) => v.map((c) => <Tag key={c}>{c}</Tag>),
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      width: 120,
      render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? '启用' : '禁用'}</Tag>,
    },
    { title: '最近触发', dataIndex: 'lastTriggeredAt', key: 'lastTriggeredAt', width: 180, render: (v: string) => v || '-' },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right' as const,
      render: (_: unknown, rule: AlertRule) => (
        <Space>
          <Button type="link" icon={<EyeOutlined />} onClick={() => { setEditingRule(rule); setDetailOpen(true); }}>
            详情
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => openEditRule(rule)}>
            编辑
          </Button>
          <Button type="link" onClick={() => toggleRuleEnabled(rule)}>
            {rule.enabled ? '禁用' : '启用'}
          </Button>
          <Popconfirm title="确认删除规则？" onConfirm={() => deleteRule(rule)} okText="确认" cancelText="取消">
            <Button type="link" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const eventColumns = [
    { title: '告警ID', dataIndex: 'id', key: 'id', width: 140 },
    { title: '级别', dataIndex: 'level', key: 'level', width: 100, render: (v: AlertLevel) => getLevelTag(v) },
    { title: '服务', dataIndex: 'service', key: 'service', width: 160, render: (v: string) => <Tag>{v}</Tag> },
    { title: '规则', dataIndex: 'ruleName', key: 'ruleName', width: 200, ellipsis: true },
    { title: '内容', dataIndex: 'message', key: 'message', width: 320, ellipsis: true },
    { title: '状态', dataIndex: 'status', key: 'status', width: 120, render: (v: AlertStatus) => getStatusTag(v) },
    { title: '发生时间', dataIndex: 'occurredAt', key: 'occurredAt', width: 180 },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right' as const,
      render: (_: unknown, evt: AlertEvent) => (
        <Space>
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => {
              setCurrentEvent(evt);
              setEventDetailOpen(true);
            }}
          >
            详情
          </Button>
          <Button type="link" disabled={evt.status !== 'OPEN'} onClick={() => ackEvent(evt)}>
            确认
          </Button>
          <Button type="link" disabled={evt.status === 'RESOLVED'} onClick={() => resolveEvent(evt)}>
            恢复
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '运维监控' }, { title: '告警管理' }]} />

      <div style={{ marginBottom: 16 }}>
        <Space align="center">
          <Title level={4} style={{ margin: 0 }}>
            告警管理
          </Title>
          <Tag icon={<BellOutlined />}>模拟数据</Tag>
        </Space>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Space wrap>
            <Select
              value={tab}
              style={{ width: 160 }}
              onChange={(v) => setTab(v)}
              options={[
                { value: 'events', label: '告警事件' },
                { value: 'rules', label: '告警规则' },
              ]}
            />

            <Form form={queryForm} layout="inline">
              <Form.Item name="level" label="级别">
                <Select placeholder="全部" allowClear style={{ width: 160 }}>
                  <Select.Option value="CRITICAL">致命</Select.Option>
                  <Select.Option value="HIGH">高</Select.Option>
                  <Select.Option value="MEDIUM">中</Select.Option>
                  <Select.Option value="LOW">低</Select.Option>
                </Select>
              </Form.Item>
              {tab === 'events' && (
                <Form.Item name="status" label="状态">
                  <Select placeholder="全部" allowClear style={{ width: 160 }}>
                    <Select.Option value="OPEN">未处理</Select.Option>
                    <Select.Option value="ACKED">已确认</Select.Option>
                    <Select.Option value="RESOLVED">已恢复</Select.Option>
                  </Select>
                </Form.Item>
              )}
              <Form.Item name="keyword" label="关键字">
                <Input placeholder="规则/服务/内容" allowClear style={{ width: 220 }} />
              </Form.Item>
            </Form>

            <Button type="primary" onClick={handleQuery}>
              搜索
            </Button>
            <Button onClick={resetQuery}>重置</Button>
            <Button loading={loading} onClick={refresh}>
              刷新
            </Button>

            {tab === 'rules' && (
              <Button type="primary" icon={<PlusOutlined />} onClick={openCreateRule}>
                新建规则
              </Button>
            )}
          </Space>
        </div>

        {tab === 'rules' ? (
          <Table
            rowKey="id"
            loading={loading}
            columns={ruleColumns}
            dataSource={rules}
            pagination={{ pageSize: 10 }}
            scroll={{ x: 'max-content' }}
          />
        ) : (
          <Table
            rowKey="id"
            loading={loading}
            columns={eventColumns}
            dataSource={events}
            pagination={{ pageSize: 10 }}
            scroll={{ x: 'max-content' }}
          />
        )}
      </Card>

      <Modal
        title={editingRule ? '编辑规则' : '新建规则'}
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={saveRule}
        okText="保存"
        cancelText="取消"
        width={860}
      >
        <Form form={ruleForm} layout="vertical">
          <Form.Item name="name" label="规则名称" rules={[{ required: true, message: '请输入规则名称' }]}>
            <Input maxLength={64} showCount />
          </Form.Item>
          <Space size={16} wrap style={{ width: '100%' }}>
            <Form.Item name="level" label="级别" rules={[{ required: true }]} style={{ width: 200 }}>
              <Select>
                <Select.Option value="CRITICAL">致命</Select.Option>
                <Select.Option value="HIGH">高</Select.Option>
                <Select.Option value="MEDIUM">中</Select.Option>
                <Select.Option value="LOW">低</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="metric" label="指标" rules={[{ required: true }]} style={{ width: 280 }}>
              <Select
                options={[
                  { value: 'cpu', label: 'cpu' },
                  { value: 'mem', label: 'mem' },
                  { value: 'errorRate', label: 'errorRate' },
                  { value: 'slowSql', label: 'slowSql' },
                ]}
              />
            </Form.Item>
            <Form.Item name="enabled" label="启用" valuePropName="checked" style={{ width: 200 }}>
              <Switch checkedChildren="启用" unCheckedChildren="禁用" />
            </Form.Item>
          </Space>
          <Form.Item name="threshold" label="阈值表达式" rules={[{ required: true, message: '请输入阈值表达式' }]}>
            <Input placeholder="例如：cpu > 80%" />
          </Form.Item>
          <Form.Item name="channel" label="通知渠道" rules={[{ required: true, message: '请选择至少一个渠道' }]}>
            <Select mode="multiple" placeholder="选择渠道">
              <Select.Option value="IN_APP">站内信</Select.Option>
              <Select.Option value="EMAIL">邮件</Select.Option>
              <Select.Option value="SMS">短信</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea autoSize={{ minRows: 3, maxRows: 8 }} maxLength={500} showCount />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="规则详情" open={detailOpen} onCancel={() => setDetailOpen(false)} footer={null} width={860}>
        {editingRule && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="ID">{editingRule.id}</Descriptions.Item>
            <Descriptions.Item label="名称">{editingRule.name}</Descriptions.Item>
            <Descriptions.Item label="级别">{getLevelTag(editingRule.level)}</Descriptions.Item>
            <Descriptions.Item label="指标">{editingRule.metric}</Descriptions.Item>
            <Descriptions.Item label="阈值">{editingRule.threshold}</Descriptions.Item>
            <Descriptions.Item label="渠道">{editingRule.channel.map((c) => <Tag key={c}>{c}</Tag>)}</Descriptions.Item>
            <Descriptions.Item label="状态">{editingRule.enabled ? '启用' : '禁用'}</Descriptions.Item>
            <Descriptions.Item label="最近触发">{editingRule.lastTriggeredAt || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{editingRule.createdAt}</Descriptions.Item>
            <Descriptions.Item label="描述">{editingRule.description || '-'}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      <Modal title="告警事件详情" open={eventDetailOpen} onCancel={() => setEventDetailOpen(false)} footer={null} width={900}>
        {currentEvent && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="告警ID">{currentEvent.id}</Descriptions.Item>
            <Descriptions.Item label="级别">{getLevelTag(currentEvent.level)}</Descriptions.Item>
            <Descriptions.Item label="服务">{currentEvent.service}</Descriptions.Item>
            <Descriptions.Item label="规则">{currentEvent.ruleName}</Descriptions.Item>
            <Descriptions.Item label="内容">{currentEvent.message}</Descriptions.Item>
            <Descriptions.Item label="状态">{getStatusTag(currentEvent.status)}</Descriptions.Item>
            <Descriptions.Item label="发生时间">{currentEvent.occurredAt}</Descriptions.Item>
            <Descriptions.Item label="确认人">{currentEvent.acknowledgedBy || '-'}</Descriptions.Item>
            <Descriptions.Item label="确认时间">{currentEvent.acknowledgedAt || '-'}</Descriptions.Item>
            <Descriptions.Item label="恢复时间">{currentEvent.resolvedAt || '-'}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Alerts;
