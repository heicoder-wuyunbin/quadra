import { useCallback, useEffect, useState } from 'react';
import { Breadcrumb, Button, Card, Descriptions, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Table, Tag, Typography } from 'antd';
import { PlusOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons';
import { monitorApi } from '@/services/api';

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

  const [rules, setRules] = useState<AlertRule[]>([]);
  const [events, setEvents] = useState<AlertEvent[]>([]);

  const fetchRules = useCallback(
    async (params?: { level?: AlertLevel; enabled?: boolean; keyword?: string }) => {
      const accessToken = localStorage.getItem('access_token');
      if (!accessToken) return;
      setLoading(true);
      try {
        const page = await monitorApi.listAlertRules(params);
        setRules((page.records || page.list || []) as AlertRule[]);
      } catch (error) {
        console.warn(error);
        message.error((error as Error)?.message || '获取告警规则失败');
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const fetchEvents = useCallback(
    async (params?: { level?: AlertLevel; status?: AlertStatus; keyword?: string; page?: number; size?: number }) => {
      const accessToken = localStorage.getItem('access_token');
      if (!accessToken) return;
      setLoading(true);
      try {
        const page = await monitorApi.listAlertEvents(params);
        setEvents((page.records || page.list || []) as AlertEvent[]);
      } catch (error) {
        console.warn(error);
        message.error((error as Error)?.message || '获取告警事件失败');
      } finally {
        setLoading(false);
      }
    },
    []
  );

  useEffect(() => {
    queryForm.setFieldsValue({ level: undefined, status: undefined, keyword: undefined });
  }, [queryForm]);

  useEffect(() => {
    if (tab === 'rules') {
      fetchRules();
      return;
    }
    fetchEvents({ page: 1, size: 50 });
  }, [tab, fetchRules, fetchEvents]);

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
    const values = queryForm.getFieldsValue();
    if (tab === 'rules') {
      await fetchRules({
        level: values.level,
        keyword: values.keyword?.trim() || undefined,
      });
      return;
    }
    await fetchEvents({
      level: values.level,
      status: values.status,
      keyword: values.keyword?.trim() || undefined,
      page: 1,
      size: 50,
    });
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
        await monitorApi.updateAlertRule(editingRule.id, values);
        message.success('规则已更新');
      } else {
        await monitorApi.createAlertRule(values);
        message.success('规则已创建');
      }
      setEditOpen(false);
      await fetchRules();
    } catch (error) {
      console.warn('saveRule failed:', error);
      message.error((error as Error)?.message || '保存失败');
    }
  };

  const toggleRuleEnabled = async (rule: AlertRule) => {
    try {
      await monitorApi.updateAlertRule(rule.id, { enabled: !rule.enabled });
      message.success(rule.enabled ? '已禁用规则' : '已启用规则');
      await fetchRules();
    } catch (error) {
      console.warn(error);
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const deleteRule = async (rule: AlertRule) => {
    try {
      await monitorApi.deleteAlertRule(rule.id);
      message.success('已删除规则');
      await fetchRules();
    } catch (error) {
      console.warn(error);
      message.error((error as Error)?.message || '删除失败');
    }
  };

  const ackEvent = async (evt: AlertEvent) => {
    if (evt.status !== 'OPEN') return;
    try {
      await monitorApi.ackAlertEvent(evt.id);
      message.success('已确认告警');
      await fetchEvents({ page: 1, size: 50 });
    } catch (error) {
      console.warn(error);
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const resolveEvent = async (evt: AlertEvent) => {
    if (evt.status === 'RESOLVED') return;
    try {
      await monitorApi.resolveAlertEvent(evt.id);
      message.success('已标记恢复');
      await fetchEvents({ page: 1, size: 50 });
    } catch (error) {
      console.warn(error);
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const handleQuery = async () => {
    const values = await queryForm.validateFields();
    if (tab === 'rules') {
      await fetchRules({
        level: values.level,
        keyword: values.keyword?.trim() || undefined,
      });
    } else {
      await fetchEvents({
        level: values.level,
        status: values.status,
        keyword: values.keyword?.trim() || undefined,
        page: 1,
        size: 50,
      });
    }
  };

  const resetQuery = () => {
    queryForm.resetFields();
    if (tab === 'rules') {
      fetchRules();
      return;
    }
    fetchEvents({ page: 1, size: 50 });
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
