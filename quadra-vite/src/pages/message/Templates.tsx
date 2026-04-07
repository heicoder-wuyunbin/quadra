import { useEffect, useMemo, useState } from 'react';
import { Breadcrumb, Button, Card, Form, Input, message, Modal, Space, Table, Tag, Typography } from 'antd';
import { PlusOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { messageApi } from '@/services/api';
import type { MessageTemplateDTO, MessageTemplateQueryParams, PageResult } from '@/services/types';

const { Title } = Typography;
const { TextArea } = Input;

const Templates: React.FC = () => {
  const [form] = Form.useForm();
  const [editForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<MessageTemplateDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<MessageTemplateQueryParams>({});
  const [editOpen, setEditOpen] = useState(false);
  const [editing, setEditing] = useState<MessageTemplateDTO | null>(null);
  const [previewOpen, setPreviewOpen] = useState(false);

  const mockTemplates: MessageTemplateDTO[] = useMemo(
    () => [
      {
        id: 1,
        name: '系统维护通知',
        description: '用于系统升级/维护提醒',
        content: '尊敬的用户：\n系统将于 {timeRange} 进行维护，期间部分功能可能不可用，敬请谅解。',
        variables: ['timeRange'],
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-10 10:00:00',
      },
      {
        id: 2,
        name: '活动通知',
        description: '运营活动触达',
        content: 'Hi {nickname}，\n{activityName} 活动开始啦！点击进入：{url}',
        variables: ['nickname', 'activityName', 'url'],
        createdAt: '2024-01-02 10:00:00',
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
      setData(mockTemplates);
      setTotal(mockTemplates.length);
      return;
    }

    setLoading(true);
    try {
      const res = await messageApi.listTemplates({
        page,
        size: pageSize,
        keyword: query.keyword,
      });
      const payload = (res.data?.data || res.data) as PageResult<MessageTemplateDTO>;
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.warn('listTemplates failed, fallback mock:', error);
      const keyword = query.keyword?.trim();
      const filtered = keyword
        ? mockTemplates.filter((t) => t.name.includes(keyword) || t.content.includes(keyword))
        : mockTemplates;
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
      keyword: values.keyword?.trim() || undefined,
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const openCreate = () => {
    setEditing(null);
    editForm.resetFields();
    setEditOpen(true);
  };

  const openEdit = (record: MessageTemplateDTO) => {
    setEditing(record);
    editForm.setFieldsValue({
      name: record.name,
      description: record.description,
      variables: record.variables?.join(',') || '',
      content: record.content,
    });
    setEditOpen(true);
  };

  const saveTemplate = async () => {
    const values = await editForm.validateFields();
    const variables = values.variables
      ? String(values.variables)
          .split(',')
          .map((s) => s.trim())
          .filter(Boolean)
      : [];

    try {
      if (editing) {
        await messageApi.updateTemplate(editing.id, {
          name: values.name,
          description: values.description,
          content: values.content,
          variables,
        });
        message.success('模板已更新');
      } else {
        await messageApi.createTemplate({
          name: values.name,
          description: values.description,
          content: values.content,
          variables,
        });
        message.success('模板已创建');
      }
      setEditOpen(false);
      fetchData();
    } catch (error) {
      console.warn('saveTemplate failed (mock ok):', error);
      message.success(editing ? '（模拟）模板已更新' : '（模拟）模板已创建');
      setEditOpen(false);
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '模板名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
    },
    {
      title: '变量',
      dataIndex: 'variables',
      key: 'variables',
      width: 240,
      render: (vars: string[] | undefined) =>
        vars && vars.length ? (
          <Space size={[4, 4]} wrap>
            {vars.map((v) => (
              <Tag key={v}>{`{${v}}`}</Tag>
            ))}
          </Space>
        ) : (
          '-'
        ),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (v: string | undefined, r: MessageTemplateDTO) =>
        dayjs(v || r.createdAt).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      fixed: 'right' as const,
      render: (_: unknown, record: MessageTemplateDTO) => (
        <Space>
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => {
              setEditing(record);
              setPreviewOpen(true);
            }}
          >
            预览
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            编辑
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: '消息推送' }, { title: '消息模板' }]} />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          消息模板
        </Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: 16 }}>
          <Form form={form} layout="inline">
            <Form.Item name="keyword" label="关键字">
              <Input placeholder="模板名称/内容" allowClear style={{ width: 260 }} />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
                <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
                  新建模板
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

      <Card style={{ marginTop: 16 }} variant="borderless">
        <Title level={5} style={{ marginTop: 0 }}>
          模板变量说明
        </Title>
        <ul style={{ marginBottom: 0 }}>
          <li>变量写法：使用 <code>{'{variable}'}</code>（示例：<code>{'{nickname}'}</code>）。</li>
          <li>变量替换通常由服务端完成；前端此处仅用于模板编辑与预览。</li>
        </ul>
      </Card>

      <Modal
        title={editing ? '编辑模板' : '新建模板'}
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={saveTemplate}
        okText="保存"
        cancelText="取消"
        width={820}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="name" label="模板名称" rules={[{ required: true, message: '请输入模板名称' }]}>
            <Input maxLength={64} showCount />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input maxLength={128} showCount />
          </Form.Item>
          <Form.Item name="variables" label="变量（逗号分隔）" tooltip="示例：nickname,url">
            <Input placeholder="nickname,activityName,url" />
          </Form.Item>
          <Form.Item name="content" label="模板内容" rules={[{ required: true, message: '请输入模板内容' }]}>
            <TextArea autoSize={{ minRows: 6, maxRows: 14 }} maxLength={2000} showCount />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="模板预览"
        open={previewOpen}
        onCancel={() => setPreviewOpen(false)}
        footer={null}
        width={720}
      >
        <div style={{ marginBottom: 8 }}>
          <strong>模板：</strong> {editing?.name || '-'}
        </div>
        <div style={{ background: '#f5f5f5', padding: 12, borderRadius: 6, whiteSpace: 'pre-wrap' }}>
          {editing?.content || '-'}
        </div>
      </Modal>
    </div>
  );
};

export default Templates;
