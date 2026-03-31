import { useState, useEffect } from 'react';
import { Table, Card, Button, Modal, Form, Input, message, Typography, Tag, Space, Switch, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, LockOutlined, StopOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { adminApi } from '@/services/api';
import type { AdminDTO, CreateAdminRequest } from '@/services/types';

const { Title } = Typography;

const Admins: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AdminDTO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalVisible, setModalVisible] = useState(false);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingAdmin, setEditingAdmin] = useState<AdminDTO | null>(null);
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();

  useEffect(() => {
    fetchData();
  }, [page, pageSize]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await adminApi.listAdmins({ page, size: pageSize });
      const records = res.data.records || res.data.list || [];
      setData(records);
      setTotal(res.data.total || 0);
    } catch (error) {
      console.error('Failed to fetch admins:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    form.resetFields();
    setIsEditMode(false);
    setEditingAdmin(null);
    setModalVisible(true);
  };

  const handleEdit = (record: AdminDTO) => {
    setIsEditMode(true);
    setEditingAdmin(record);
    form.setFieldsValue({
      username: record.username,
      realName: record.realName,
    });
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (isEditMode && editingAdmin) {
        await adminApi.updateAdmin(editingAdmin.id!, values);
        message.success('更新成功');
      } else {
        await adminApi.createAdmin(values as CreateAdminRequest);
        message.success('创建成功');
      }
      setModalVisible(false);
      fetchData();
    } catch (error) {
      console.error('Failed to save admin:', error);
    }
  };

  const handlePasswordChange = (record: AdminDTO) => {
    setEditingAdmin(record);
    passwordForm.resetFields();
    setPasswordModalVisible(true);
  };

  const handlePasswordSubmit = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (editingAdmin) {
        await adminApi.updateAdminPassword(editingAdmin.id, values);
        message.success('密码修改成功');
        setPasswordModalVisible(false);
      }
    } catch (error) {
      console.error('Failed to update password:', error);
    }
  };

  const handleStatusChange = async (record: AdminDTO, newStatus: number) => {
    try {
      await adminApi.updateAdminStatus(record.id, newStatus);
      message.success(newStatus === 1 ? '已启用' : '已禁用');
      fetchData();
    } catch (error) {
      console.error('Failed to update status:', error);
    }
  };

  const columns = [
    {
      title: '管理员 ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '真实姓名',
      dataIndex: 'realName',
      key: 'realName',
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
      render: (date: string) => new Date(date).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: any, record: AdminDTO) => (
        <Space size="small">
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" icon={<LockOutlined />} onClick={() => handlePasswordChange(record)}>
            密码
          </Button>
          {record.id !== 1 && (
            <Popconfirm
              title={record.status === 1 ? "确定要禁用此管理员吗？" : "确定要启用此管理员吗？"}
              onConfirm={() => handleStatusChange(record, record.status === 1 ? 0 : 1)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="link" icon={record.status === 1 ? <StopOutlined /> : <CheckCircleOutlined />}>
                {record.status === 1 ? '禁用' : '启用'}
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={2} style={{ margin: 0 }}>管理员管理</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          创建管理员
        </Button>
      </div>
      
      <Card>
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize || 10);
            },
          }}
        />
      </Card>

      <Modal
        title={isEditMode ? '编辑管理员' : '创建管理员'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        okText="确认"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" disabled={isEditMode} />
          </Form.Item>
          {!isEditMode && (
            <Form.Item
              name="password"
              label="密码"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password placeholder="请输入密码" />
            </Form.Item>
          )}
          <Form.Item
            name="realName"
            label="真实姓名"
            rules={[{ required: true, message: '请输入真实姓名' }]}
          >
            <Input placeholder="请输入真实姓名" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="修改密码"
        open={passwordModalVisible}
        onOk={handlePasswordSubmit}
        onCancel={() => setPasswordModalVisible(false)}
        okText="确认"
        cancelText="取消"
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item
            name="password"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码长度不能少于 6 位' }
            ]}
          >
            <Input.Password placeholder="请输入新密码" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Admins;
