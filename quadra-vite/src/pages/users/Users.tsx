import { useEffect, useState } from 'react';
import { Card, Typography, Table, Form, Input, Select, Button, Space, Tag, Modal, Descriptions, message, Popconfirm, Spin } from 'antd';
import { adminApi } from '@/services/api';
import type { UserAdminDTO, UserDetailDTO, PageResult } from '@/services/types';

const { Title } = Typography;

interface QueryState {
  mobile?: string;
  status?: number;
}

const Users: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<UserAdminDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [query, setQuery] = useState<QueryState>({});
  const [detailOpen, setDetailOpen] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailData, setDetailData] = useState<UserDetailDTO | null>(null);

  useEffect(() => {
    fetchData();
  }, [page, pageSize, query]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await adminApi.listUsers({
        mobile: query.mobile,
        status: query.status,
        page,
        size: pageSize,
      });
      const records =
        (res.data as PageResult<UserAdminDTO>).records || res.data.list || [];
      setData(records);
      setTotal(res.data.total || 0);
    } catch (error) {
      console.error('Failed to fetch users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    const values = await form.validateFields();
    setPage(1);
    setQuery({
      mobile: values.mobile?.trim() || undefined,
      status: values.status ?? undefined,
    });
  };

  const handleReset = () => {
    form.resetFields();
    setPage(1);
    setQuery({});
  };

  const openDetail = async (record: UserAdminDTO) => {
    setDetailOpen(true);
    setDetailLoading(true);
    try {
      const res = await adminApi.getUserDetail(record.id);
      setDetailData(res.data);
    } catch (error) {
      console.error('Failed to fetch user detail:', error);
      message.error('获取用户详情失败');
      setDetailOpen(false);
    } finally {
      setDetailLoading(false);
    }
  };

  const handleToggleStatus = async (record: UserAdminDTO) => {
    const nextStatus = record.status === 1 ? 0 : 1;
    try {
      await adminApi.updateUserStatus(record.id, nextStatus);
      message.success(nextStatus === 1 ? '已启用用户' : '已禁用用户');
      fetchData();
    } catch (error) {
      console.error('Failed to update user status:', error);
    }
  };

  const handleResetPassword = (record: UserAdminDTO) => {
    Modal.confirm({
      title: '确认重置密码',
      content: `确定要重置用户 ${record.mobile} 的密码吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await adminApi.resetUserPassword(record.id);
        Modal.success({
          title: '密码已重置',
          content: `新密码：${res.data.newPassword}`,
        });
      },
    });
  };

  const columns = [
    {
      title: '用户 ID',
      dataIndex: 'id',
      key: 'id',
      width: 120,
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      key: 'mobile',
      width: 160,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
      width: 160,
      render: (value: string) => value || '-',
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 100,
      render: (value: number) => {
        if (value === 1) return '男';
        if (value === 2) return '女';
        return '-';
      },
    },
    {
      title: '城市',
      dataIndex: 'city',
      key: 'city',
      width: 140,
      render: (value: string) => value || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (value: number) => (
        <Tag color={value === 1 ? 'green' : 'red'}>
          {value === 1 ? '正常' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '注册时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (value: string) => (value ? new Date(value).toLocaleString() : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_: unknown, record: UserAdminDTO) => (
        <Space>
          <Button type="link" onClick={() => openDetail(record)}>
            详情
          </Button>
          <Popconfirm
            title={record.status === 1 ? '确认禁用该用户？' : '确认启用该用户？'}
            okText="确认"
            cancelText="取消"
            onConfirm={() => handleToggleStatus(record)}
          >
            <Button type="link">
              {record.status === 1 ? '禁用' : '启用'}
            </Button>
          </Popconfirm>
          <Button type="link" onClick={() => handleResetPassword(record)}>
            重置密码
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Title level={2}>用户管理</Title>
      <Card style={{ marginBottom: 16 }}>
        <Form form={form} layout="inline">
          <Form.Item name="mobile" label="手机号">
            <Input placeholder="支持模糊搜索" allowClear style={{ width: 200 }} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select
              allowClear
              placeholder="全部"
              style={{ width: 120 }}
              options={[
                { label: '正常', value: 1 },
                { label: '禁用', value: 0 },
              ]}
            />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" onClick={handleSearch}>
                查询
              </Button>
              <Button onClick={handleReset}>重置</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>

      <Card>
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
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize ?? 10);
            },
          }}
        />
      </Card>

      <Modal
        title="用户详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        destroyOnClose
      >
        <Spin spinning={detailLoading}>
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="用户 ID">{detailData?.id ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="手机号">{detailData?.mobile ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="昵称">{detailData?.nickname ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="性别">
              {detailData?.gender === 1 ? '男' : detailData?.gender === 2 ? '女' : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="城市">{detailData?.city ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="收入">{detailData?.income ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="行业">{detailData?.profession ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="婚姻">
              {detailData?.marriage === 1 ? '离异' : detailData?.marriage === 2 ? '丧偶' : '未婚'}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              {detailData?.status === 1 ? '正常' : '禁用'}
            </Descriptions.Item>
            <Descriptions.Item label="注册时间">
              {detailData?.createdAt ? new Date(detailData.createdAt).toLocaleString() : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="更新时间">
              {detailData?.updatedAt ? new Date(detailData.updatedAt).toLocaleString() : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="标签">
              {detailData?.tags ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="通知设置">
              点赞 {detailData?.likeNotification ?? '-'} / 评论 {detailData?.commentNotification ?? '-'} / 系统 {detailData?.systemNotification ?? '-'}
            </Descriptions.Item>
          </Descriptions>
        </Spin>
      </Modal>
    </div>
  );
};

export default Users;
