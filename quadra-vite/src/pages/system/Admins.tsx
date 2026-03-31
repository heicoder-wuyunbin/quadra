import { useState, useEffect } from 'react';
import { Table, Card, Button, Modal, Form, Input, message, Typography, Tag, Space } from 'antd';
import { PlusOutlined, EditOutlined } from '@ant-design/icons';
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
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingAdmin, setEditingAdmin] = useState<AdminDTO | null>(null);
  const [form] = Form.useForm();

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
      width: 100,
      render: (_: any, record: AdminDTO) => (
        <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
          编辑
        </Button>
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
    </div>
  );
};

export default Admins;
