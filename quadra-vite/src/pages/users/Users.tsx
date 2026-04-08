import { useEffect, useState, useCallback, Key } from 'react';
import { Card, Typography, Table, Form, Input, Select, Button, Space, Tag, Modal, Descriptions, message, Popconfirm, Breadcrumb, Divider } from 'antd';
import { SearchOutlined, DeleteOutlined } from '@ant-design/icons';
import { adminApi } from '@/services/api';
import type { UserAdminDTO, UserDetailDTO } from '@/services/types';
import dayjs from 'dayjs';

const { Title, Text } = Typography;

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
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const fetchData = useCallback(async () => {
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      return;
    }

    setLoading(true);
    try {
      const payload = await adminApi.listUsers({
        mobile: query.mobile,
        status: query.status,
        page,
        size: pageSize,
      });
      const records = payload.records || payload.list || [];
      setData(records);
      setTotal(payload.total || 0);
    } catch (error) {
      console.error('Failed to fetch users:', error);
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, query]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

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
      const detail = await adminApi.getUserDetail(record.id);
      setDetailData(detail);
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
          content: `新密码：${res.newPassword}`,
        });
      },
    });
  };

  const handleBatchStatusChange = async (status: number) => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要操作的用户');
      return;
    }

    try {
      await adminApi.batchUpdateUserStatus(selectedRowKeys as number[], status);
      message.success(status === 1 ? '已批量启用' : '已批量禁用');
      setSelectedRowKeys([]);
      fetchData();
    } catch (error) {
      console.error('Failed to batch update status:', error);
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的用户');
      return;
    }

    try {
      await adminApi.batchDeleteUsers(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      fetchData();
    } catch (error) {
      console.error('Failed to batch delete:', error);
    }
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    fixed: 'left',
    columnWidth: 50,
  };

  const columns = [
    {
      title: '用户 ID',
      dataIndex: 'id',
      key: 'id',
      width: 180,
      render: (id: string) => <Text copyable>{id}</Text>,
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
      render: (value: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right',
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
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '用户管理' },
        { title: '用户列表' },
      ]} />

      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>用户列表</Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px' }}>
          <Form form={form} layout="inline">
            <Form.Item name="mobile" label="手机号">
              <Input
                placeholder="支持模糊搜索"
                allowClear
                style={{ width: 200 }}
                prefix={<SearchOutlined />}
                onPressEnter={handleSearch}
              />
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
                <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
              </Space>
            </Form.Item>
          </Form>
        </div>

        {selectedRowKeys.length > 0 && (
          <div style={{ padding: '12px 16px', background: '#fafafa', borderTop: '1px solid #f0f0f0' }}>
            <Space>
              <span style={{ color: 'rgba(0, 0, 0, 0.45)' }}>已选择 {selectedRowKeys.length} 项</span>
              <Divider type="vertical" />
              <Button 
                danger
                size="small"
                onClick={() => handleBatchStatusChange(0)}
              >
                批量禁用
              </Button>
              <Button 
                size="small"
                onClick={() => handleBatchStatusChange(1)}
              >
                批量启用
              </Button>
              <Button 
                danger
                icon={<DeleteOutlined />}
                size="small"
                onClick={handleBatchDelete}
              >
                批量删除
              </Button>
            </Space>
          </div>
        )}

        <Table
            rowKey="id"
            rowSelection={rowSelection}
            loading={loading}
            dataSource={data}
            columns={columns}
            pagination={{
              current: page,
              pageSize,
              total,
              showSizeChanger: true,
              showTotal: (total) => `共 ${total} 条`,
              pageSizeOptions: ['10', '20', '50'],
              onChange: (page, pageSize) => {
                setPage(page);
                setPageSize(pageSize);
              },
            }}
            scroll={{ x: 'max-content' }}
            size="middle"
            style={{ overflow: 'hidden' }}
          />
      </Card>

      <Modal
        title="用户详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={720}
        destroyOnClose
      >
        {detailLoading ? (
          <div style={{ padding: '24px 0', textAlign: 'center' }}>加载中...</div>
        ) : (
          detailData && (
            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="用户ID">{detailData.id}</Descriptions.Item>
              <Descriptions.Item label="手机号">{detailData.mobile}</Descriptions.Item>
              <Descriptions.Item label="昵称">{detailData.nickname || '-'}</Descriptions.Item>
              <Descriptions.Item label="性别">
                {detailData.gender === 1 ? '男' : detailData.gender === 2 ? '女' : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="城市">{detailData.city || '-'}</Descriptions.Item>
              <Descriptions.Item label="状态">{detailData.status === 1 ? '正常' : '禁用'}</Descriptions.Item>
              <Descriptions.Item label="注册时间" span={2}>
                {detailData.createdAt ? dayjs(detailData.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="更新时间" span={2}>
                {detailData.updatedAt ? dayjs(detailData.updatedAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="职业">{detailData.profession || '-'}</Descriptions.Item>
              <Descriptions.Item label="收入">{detailData.income || '-'}</Descriptions.Item>
              <Descriptions.Item label="婚姻">{detailData.marriage ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="标签">{detailData.tags || '-'}</Descriptions.Item>
            </Descriptions>
          )
        )}
      </Modal>
    </div>
  );
};

export default Users;
