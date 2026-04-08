import { useCallback, useEffect, useState } from 'react';
import { Breadcrumb, Button, Card, DatePicker, Form, Input, message, Modal, Select, Space, Typography } from 'antd';
import dayjs, { Dayjs } from 'dayjs';
import { EyeOutlined, SendOutlined } from '@ant-design/icons';
import { messageApi } from '@/services/api';
import type { MessageTemplateDTO, NoticePriority, NoticeTargetType, NoticeType, SendNoticeRequest } from '@/services/types';

const { Title } = Typography;
const { TextArea } = Input;

const SendNotice: React.FC = () => {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [templatesLoading, setTemplatesLoading] = useState(false);
  const [templates, setTemplates] = useState<MessageTemplateDTO[]>([]);

  const fetchTemplates = useCallback(async () => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) return;
    setTemplatesLoading(true);
    try {
      const page = await messageApi.listTemplates({ page: 1, size: 200 });
      setTemplates(page.records || page.list || []);
    } catch (error) {
      console.warn('listTemplates failed:', error);
      message.error((error as Error)?.message || '获取模板列表失败');
    } finally {
      setTemplatesLoading(false);
    }
  }, []);

  useEffect(() => {
    // 默认值
    form.setFieldsValue({
      type: 'SYSTEM' as NoticeType,
      targetType: 'ALL' as NoticeTargetType,
      priority: 'NORMAL' as NoticePriority,
      sendMode: 'NOW',
    });
  }, [form]);

  useEffect(() => {
    fetchTemplates();
  }, [fetchTemplates]);

  const parseTargetIds = (value?: string): number[] | undefined => {
    if (!value) return undefined;
    const ids = value
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean)
      .map((s) => Number(s))
      .filter((n) => Number.isFinite(n) && n > 0);
    return ids.length ? ids : undefined;
  };

  const handleTemplateChange = (templateId?: number) => {
    if (!templateId) return;
    const tpl = templates.find((t) => t.id === templateId);
    if (!tpl) return;
    const currentTitle: string | undefined = form.getFieldValue('title');
    const currentContent: string | undefined = form.getFieldValue('content');
    form.setFieldsValue({
      title: currentTitle || tpl.name,
      content: currentContent || tpl.content,
    });
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const targetType = values.targetType as NoticeTargetType;
    const sendMode = values.sendMode as 'NOW' | 'SCHEDULE';
    const scheduledAt: Dayjs | undefined = values.scheduledAt;

    if (sendMode === 'SCHEDULE' && !scheduledAt) {
      message.warning('请选择定时发送时间');
      return;
    }

    const payload: SendNoticeRequest = {
      title: values.title,
      content: values.content,
      type: values.type,
      targetType,
      targetIds: targetType === 'USER' ? parseTargetIds(values.targetIds) : undefined,
      priority: values.priority,
      scheduledAt: sendMode === 'SCHEDULE' ? scheduledAt?.format('YYYY-MM-DD HH:mm:ss') : undefined,
      templateId: values.templateId || undefined,
    };

    setSubmitting(true);
    try {
      await messageApi.sendNotice(payload);
      message.success(sendMode === 'SCHEDULE' ? '已创建定时发送任务' : '发送成功');
      form.resetFields();
      form.setFieldsValue({
        type: 'SYSTEM',
        targetType: 'ALL',
        priority: 'NORMAL',
        sendMode: 'NOW',
      });
    } catch (error) {
      console.warn('sendNotice failed:', error);
      message.error((error as Error)?.message || '发送失败');
    } finally {
      setSubmitting(false);
    }
  };

  const targetType: NoticeTargetType = Form.useWatch('targetType', form);
  const sendMode: 'NOW' | 'SCHEDULE' = Form.useWatch('sendMode', form);

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb
        items={[
          { title: '消息推送' },
          { title: '发送站内信' },
        ]}
      />

      <div style={{ marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          发送站内信
        </Title>
      </div>

      <Card variant="borderless">
        <Form form={form} layout="vertical">
          <Form.Item label="接收对象" required>
            <Space wrap>
              <Form.Item name="targetType" noStyle rules={[{ required: true, message: '请选择接收对象' }]}>
                <Select style={{ width: 200 }}>
                  <Select.Option value="ALL">全体用户</Select.Option>
                  <Select.Option value="USER">指定用户</Select.Option>
                  <Select.Option value="GROUP">用户组（预留）</Select.Option>
                </Select>
              </Form.Item>

              {targetType === 'USER' && (
                <Form.Item
                  name="targetIds"
                  noStyle
                  rules={[{ required: true, message: '请输入用户 ID（逗号分隔）' }]}
                >
                  <Input style={{ width: 360 }} placeholder="用户ID，逗号分隔：10001,10002" />
                </Form.Item>
              )}

              {targetType === 'GROUP' && (
                <Form.Item name="groupId" noStyle>
                  <Input style={{ width: 260 }} placeholder="用户组ID（预留）" />
                </Form.Item>
              )}
            </Space>
          </Form.Item>

          <Space size={16} wrap>
            <Form.Item label="消息类型" name="type" rules={[{ required: true, message: '请选择消息类型' }]} style={{ width: 200 }}>
              <Select>
                <Select.Option value="SYSTEM">系统通知</Select.Option>
                <Select.Option value="ACTIVITY">活动通知</Select.Option>
                <Select.Option value="REMINDER">提醒</Select.Option>
                <Select.Option value="CUSTOM">自定义</Select.Option>
              </Select>
            </Form.Item>

            <Form.Item label="优先级" name="priority" rules={[{ required: true, message: '请选择优先级' }]} style={{ width: 200 }}>
              <Select>
                <Select.Option value="LOW">低</Select.Option>
                <Select.Option value="NORMAL">普通</Select.Option>
                <Select.Option value="HIGH">高</Select.Option>
                <Select.Option value="URGENT">紧急</Select.Option>
              </Select>
            </Form.Item>

            <Form.Item label="选择模板（可选）" name="templateId" style={{ width: 320 }}>
              <Select
                placeholder="不使用模板"
                allowClear
                onChange={(id) => handleTemplateChange(id)}
                loading={templatesLoading}
                options={templates.map((t) => ({ value: t.id, label: `${t.name}${t.description ? `（${t.description}）` : ''}` }))}
              />
            </Form.Item>
          </Space>

          <Form.Item label="标题" name="title" rules={[{ required: true, message: '请输入标题' }]}>
            <Input placeholder="请输入站内信标题" maxLength={64} showCount />
          </Form.Item>

          <Form.Item label="内容" name="content" rules={[{ required: true, message: '请输入内容' }]}>
            <TextArea placeholder="请输入消息内容（支持模板变量，如：{nickname}）" autoSize={{ minRows: 6, maxRows: 14 }} maxLength={2000} showCount />
          </Form.Item>

          <Form.Item label="发送方式" required>
            <Space wrap>
              <Form.Item name="sendMode" noStyle rules={[{ required: true }]}>
                <Select style={{ width: 200 }}>
                  <Select.Option value="NOW">立即发送</Select.Option>
                  <Select.Option value="SCHEDULE">定时发送</Select.Option>
                </Select>
              </Form.Item>

              {sendMode === 'SCHEDULE' && (
                <Form.Item
                  name="scheduledAt"
                  noStyle
                  rules={[{ required: true, message: '请选择定时发送时间' }]}
                >
                  <DatePicker
                    showTime
                    style={{ width: 280 }}
                    placeholder="选择发送时间"
                    disabledDate={(current) => !!current && current < dayjs().startOf('day')}
                  />
                </Form.Item>
              )}
            </Space>
          </Form.Item>

          <Space>
            <Button icon={<EyeOutlined />} onClick={() => setPreviewOpen(true)}>
              预览
            </Button>
            <Button type="primary" icon={<SendOutlined />} loading={submitting} onClick={handleSubmit}>
              {sendMode === 'SCHEDULE' ? '创建定时任务' : '发送'}
            </Button>
          </Space>
        </Form>
      </Card>

      <Modal
        title="站内信预览"
        open={previewOpen}
        onCancel={() => setPreviewOpen(false)}
        footer={null}
        width={720}
      >
        <div style={{ marginBottom: 8 }}>
          <strong>标题：</strong> {form.getFieldValue('title') || '-'}
        </div>
        <div style={{ marginBottom: 8 }}>
          <strong>接收对象：</strong> {form.getFieldValue('targetType') || '-'}
        </div>
        <div style={{ marginBottom: 8 }}>
          <strong>发送方式：</strong> {form.getFieldValue('sendMode') === 'SCHEDULE' ? '定时' : '立即'}
        </div>
        <div style={{ background: '#f5f5f5', padding: 12, borderRadius: 6, whiteSpace: 'pre-wrap' }}>
          {form.getFieldValue('content') || '-'}
        </div>
      </Modal>
    </div>
  );
};

export default SendNotice;
