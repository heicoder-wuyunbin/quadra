import { useState, useEffect } from 'react';
import type { Key } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Card, Button, Form, Input, Select, Tag, Space, message, Modal, Popconfirm, Typography } from 'antd';
import { SearchOutlined, PlusOutlined, LockOutlined, StopOutlined, CheckCircleOutlined, EyeOutlined } from '@ant-design/icons';
import { userApi } from '@/services/api';
import type { UserAdminDTO } from '@/services/types';

const { Title } = Typography;
const { Search } = Input;

interface UserQueryParams {
  page: number;
  size: number;
  mobile?: string;
  status?: number;
}

const Users: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<UserAdminDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [form] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const fetchData = async (params: UserQueryParams = { page, size: pageSize }) => {
    setLoading(true);
    try {
      const response = await userApi.getUsers(params);
      if (response.data) {
        setData(response.data.records || []);
        setTotal(response.data.total || 0);
        setPage(response.data.current || params.page);
      }
    } catch (error) {
      console.error('Failed to fetch users:', error);
      message.error('获取用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = (values: any) => {
    fetchData({
      page: 1,
      size: pageSize,
      mobile: values.mobile,
      status: values.status,
    });
  };

  const handleReset = () => {
    form.resetFields();
    fetchData({ page: 1, size: pageSize });
  };

  const handleStatusChange = async (record: UserAdminDTO, newStatus: number) => {
    try {
      await userApi.updateUserStatus(record.id, newStatus);
      message.success(newStatus === 1 ? '已启用' : '已禁用');
      fetchData();
    } catch (error) {
      console.error('Failed to update status:', error);
      message.error('操作失败');
    }
  };

  const handleResetPassword = async (record: UserAdminDTO) => {
    try {
      const response = await userApi.resetUserPassword(record.id);
      const newPassword = response.data?.newPassword;
      Modal.success({
        title: '密码已重置',
        content: `新密码为：${newPassword}`,
        okText: '确定',
      });
    } catch (error) {
      console.error('Failed to reset password:', error);
      message.error('重置密码失败');
    }
  };

  const handleViewDetail = (record: UserAdminDTO) => {
    navigate(`/user/profile/${record.id}`);
  };

  const columns = [
    {
      title: '用户 ID',
      dataIndex: 'id',
      key: 'id',
      width: 150,
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      key: 'mobile',
      width: 130,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
      width: 120,
      render: (text: string) => text || '-',
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 80,
      render: (gender: number) => {
        if (gender === 1) return '男';
        if (gender === 2) return '女';
        return '未知';
      },
    },
    {
      title: '城市',
      dataIndex: 'city',
      key: 'city',
      width: 100,
      render: (text: string) => text || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '正常' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (text: string) => {
        if (!text) return '-';
        return new Date(text).toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
        });
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      fixed: 'right' as const,
      render: (_: any, record: UserAdminDTO) => (
        <Space size="small">
          <Button type="link" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          <Button type="link" icon={<LockOutlined />} onClick={() => handleResetPassword(record)}>
            密码
          </Button>
          <Popconfirm
            title={record.status === 1 ? '确定要禁用此用户吗？' : '确定要启用此用户吗？'}
            onConfirm={() => handleStatusChange(record, record.status === 1 ? 0 : 1)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" icon={record.status === 1 ? <StopOutlined /> : <CheckCircleOutlined />}>
              {record.status === 1 ? '禁用' : '启用'}
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Title level={2} style={{ marginBottom: 16 }}>用户管理</Title>
      
      <Card style={{ marginBottom: 16 }}>
        <Form form={form} layout="inline" onFinish={handleSearch}>
          <Form.Item name="mobile" label="手机号">
            <Search placeholder="请输入手机号" allowClear style={{ width: 200 }} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select placeholder="请选择状态" allowClear style={{ width: 120 }}>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                搜索
              </Button>
              <Button onClick={handleReset}>重置</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          rowSelection={{
            selectedRowKeys,
            onChange: (newSelectedRowKeys: Key[]) => {
              setSelectedRowKeys(newSelectedRowKeys);
            },
          }}
          loading={loading}
          scroll={{ x: 1400 }}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50'],
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize);
              fetchData({ page, size: pageSize });
            },
          }}
        />
      </Card>
    </div>
  );
};

export default Users;
