import { useState, useEffect } from 'react'
import { Card, Table, Tag, Space, Button, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { ContentService } from '@/api/generated'
import type { TimelineItemDTO } from '@/api/generated/models/TimelineItemDTO'
import { EditOutlined, DeleteOutlined } from '@ant-design/icons'

const ContentList = () => {
  const [loading, setLoading] = useState(false)
  const [movements, setMovements] = useState<TimelineItemDTO[]>([])

  useEffect(() => {
    fetchMovements()
  }, [])

  const fetchMovements = async () => {
    setLoading(true)
    try {
      const response = await ContentService.pullTimeline(1, 20)
      if (response.code === 0 && response.data) {
        setMovements(response.data.list || [])
      }
    } catch (error: any) {
      message.error('加载失败：' + (error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = (id: number) => {
    // TODO: 调用删除 API
    message.success('删除成功')
  }

  const columns: ColumnsType<TimelineItemDTO> = [
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
      render: (state?: number) => {
        const stateMap: Record<number, { color: string; text: string }> = {
          0: { color: 'default', text: '草稿' },
          1: { color: 'success', text: '已发布' },
          2: { color: 'error', text: '已删除' },
        }
        const config = stateMap[state || 0] || { color: 'default', text: '未知' }
        return <Tag color={config.color}>{config.text}</Tag>
      },
    },
    {
      title: '点赞数',
      dataIndex: 'likeCount',
      width: 80,
      sorter: (a, b) => (a.likeCount || 0) - (b.likeCount || 0),
    },
    {
      title: '评论数',
      dataIndex: 'commentCount',
      width: 80,
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
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.id || 0)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <Card title="内容列表">
      <Table
        columns={columns}
        dataSource={movements}
        rowKey="id"
        loading={loading}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
      />
    </Card>
  )
}

export default ContentList
