import { useState } from 'react'
import { Card, Table, Button, Space, Tag, Input, Modal, Form, message } from 'antd'
import { SearchOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'

interface Movement {
  id: number
  userId: number
  textContent: string
  state: number
  likeCount: number
  commentCount: number
  shareCount: number
  createTime: string
}

const ContentManagement = () => {
  const [searchText, setSearchText] = useState('')
  const [modalVisible, setModalVisible] = useState(false)
  const [form] = Form.useForm()

  const columns: ColumnsType<Movement> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '用户 ID',
      dataIndex: 'userId',
      width: 100,
    },
    {
      title: '内容',
      dataIndex: 'textContent',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'state',
      width: 100,
      render: (state: number) => {
        const stateMap: Record<number, { color: string; text: string }> = {
          0: { color: 'default', text: '草稿' },
          1: { color: 'success', text: '已发布' },
          2: { color: 'error', text: '已删除' },
        }
        const config = stateMap[state] || { color: 'default', text: '未知' }
        return <Tag color={config.color}>{config.text}</Tag>
      },
    },
    {
      title: '点赞',
      dataIndex: 'likeCount',
      width: 80,
      sorter: (a, b) => a.likeCount - b.likeCount,
    },
    {
      title: '评论',
      dataIndex: 'commentCount',
      width: 80,
    },
    {
      title: '分享',
      dataIndex: 'shareCount',
      width: 80,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      sorter: (a, b) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime(),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ]

  const handleEdit = (record: Movement) => {
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = (record: Movement) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除动态 ID: ${record.id} 吗？`,
      onOk: async () => {
        try {
          // TODO: 调用删除 API
          message.success('删除成功')
        } catch (error) {
          message.error('删除失败')
        }
      },
    })
  }

  return (
    <div>
      <Card
        title="内容管理"
        extra={
          <Space>
            <Input
              placeholder="搜索内容"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ width: 200 }}
              prefix={<SearchOutlined />}
            />
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setModalVisible(true)}
            >
              发布内容
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={[]}
          rowKey="id"
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>

      <Modal
        title="发布/编辑动态"
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false)
          form.resetFields()
        }}
        onOk={() => {
          form.submit()
        }}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="textContent"
            label="内容"
            rules={[{ required: true, message: '请输入内容' }]}
          >
            <Input.TextArea rows={4} placeholder="请输入动态内容" />
          </Form.Item>
          <Form.Item name="state" label="状态">
            <Input placeholder="状态" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ContentManagement
