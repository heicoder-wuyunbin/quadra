import { useState, useEffect } from 'react';
import { Card, Table, Typography, Space, Button, Input, Form, message, Modal, Select, Popconfirm } from 'antd';
import { SearchOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { userApi } from '@/services/api';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;

interface BlacklistRecord {
  id: number;
  userId: number;
  targetUserId: number;
  targetUserMobile?: string;
  targetUserNickname?: string;
  reason?: string;
  createdAt: string;
}

interface UserOption {
  id: number;
  mobile: string;
  nickname?: string;
}

const Blacklist: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<BlacklistRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchMobile, setSearchMobile] = useState('');
  const [isAddModalVisible, setIsAddModalVisible] = useState(false);
  const [adding, setAdding] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState<number | undefined>();
  const [reason, setReason] = useState('');
  const [userOptions, setUserOptions] = useState<UserOption[]>([]);
  const [searchUserLoading, setSearchUserLoading] = useState(false);
  const [searchUserText, setSearchUserText] = useState('');

  const fetchData = async (params: { page: number; size: number; mobile?: string } = { page, size: pageSize }) => {
    setLoading(true);
    try {
      const response = await userApi.getBlacklist(params.page, params.size, params.mobile);
      if (response.data) {
        setData(response.data.records || []);
        setTotal(response.data.total || 0);
      }
    } catch (error) {
      message.error('获取黑名单列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = () => {
    setPage(1);
    fetchData({ page: 1, size: pageSize, mobile: searchMobile });
  };

  const handleReset = () => {
    setSearchMobile('');
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const handleRemoveFromBlacklist = async (targetUserId: number) => {
    try {
      await userApi.removeFromBlacklist(targetUserId);
      message.success('移除成功');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('移除失败');
    }
  };

  const handleOpenAddModal = async () => {
    setIsAddModalVisible(true);
    setSelectedUserId(undefined);
    setReason('');
    setSearchUserText('');
    setUserOptions([]);
  };

  const handleSearchUsers = async (value: string) => {
    setSearchUserText(value);
    if (!value.trim()) {
      setUserOptions([]);
      return;
    }

    setSearchUserLoading(true);
    try {
      const response = await userApi.getUsers({ page: 1, size: 20, mobile: value });
      if (response.data && response.data.records) {
        const options = response.data.records.map((user) => ({
          id: user.id,
          mobile: user.mobile,
          nickname: user.nickname,
        }));
        setUserOptions(options);
      }
    } catch (error) {
      console.error('搜索用户失败:', error);
    } finally {
      setSearchUserLoading(false);
    }
  };

  const handleAddToBlacklist = async () => {
    if (!selectedUserId) {
      message.warning('请选择要拉黑的用户');
      return;
    }

    setAdding(true);
    try {
      await userApi.addToBlacklist(selectedUserId, reason);
      message.success('添加成功');
      setIsAddModalVisible(false);
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error('添加失败');
    } finally {
      setAdding(false);
    }
  };

  const columns: ColumnsType<BlacklistRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      fixed: 'left',
    },
    {
      title: '目标用户 ID',
      dataIndex: 'targetUserId',
      key: 'targetUserId',
      width: 120,
      render: (id: number) => <Text copyable>{id}</Text>,
    },
    {
      title: '手机号',
      dataIndex: 'targetUserMobile',
      key: 'targetUserMobile',
      width: 130,
    },
    {
      title: '昵称',
      dataIndex: 'targetUserNickname',
      key: 'targetUserNickname',
      width: 150,
    },
    {
      title: '拉黑原因',
      dataIndex: 'reason',
      key: 'reason',
      width: 200,
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => dayjs(createdAt).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_: any, record: BlacklistRecord) => (
        <Space size="small">
          <Popconfirm
            title="确认移除"
            description="确定要将该用户从黑名单中移除吗？"
            onConfirm={() => handleRemoveFromBlacklist(record.targetUserId)}
            okText="确认"
            cancelText="取消"
          >
            <Button 
              type="link" 
              danger 
              size="small" 
              icon={<DeleteOutlined />}
            >
              移除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '用户管理' },
        { title: '黑名单管理' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>黑名单管理</Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px' }}>
          <Form layout="inline">
            <Form.Item label="手机号">
              <Input
                placeholder="请输入手机号"
                value={searchMobile}
                onChange={(e) => setSearchMobile(e.target.value)}
                onPressEnter={handleSearch}
                style={{ width: 200 }}
                prefix={<SearchOutlined />}
              />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
                <Button 
                  type="primary" 
                  onClick={handleOpenAddModal} 
                  icon={<PlusOutlined />}
                >
                  添加黑名单
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </div>

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
            pageSizeOptions: ['10', '20', '50', '100'],
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize);
              fetchData({ page, size: pageSize });
            },
          }}
          scroll={{ x: 1200 }}
          size="middle"
        />
      </Card>

      <Modal
        title="添加黑名单"
        open={isAddModalVisible}
        onOk={handleAddToBlacklist}
        onCancel={() => setIsAddModalVisible(false)}
        confirmLoading={adding}
        width={600}
      >
        <Form layout="vertical">
          <Form.Item
            label="选择用户"
            required
            rules={[{ required: true, message: '请选择要拉黑的用户' }]}
          >
            <Select
              placeholder="请输入手机号或昵称搜索用户"
              value={selectedUserId}
              onChange={setSelectedUserId}
              onSearch={handleSearchUsers}
              showSearch
              filterOption={false}
              notContentText={searchUserLoading ? '加载中...' : '未找到用户'}
              loading={searchUserLoading}
              style={{ width: '100%' }}
            >
              {userOptions.map((user) => (
                <Option key={user.id} value={user.id}>
                  {user.nickname || '无昵称'} - {user.mobile}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="拉黑原因">
            <Input.TextArea
              rows={4}
              placeholder="请输入拉黑原因（选填）"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Blacklist;
