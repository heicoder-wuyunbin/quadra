import { useState, useEffect, useCallback, Key } from 'react';
import { Card, Table, Typography, Space, Button, Form, message, Tag, Popconfirm, Select, Modal, Descriptions, Breadcrumb } from 'antd';
import { SearchOutlined, CheckOutlined, CloseOutlined, EyeOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { contentAdminApi } from '@/services/api';
import type { PageResult } from '@/services/types';

const { Title, Text } = Typography;
const { Option } = Select;

interface ReportRecord {
  id: number;
  reportType: string;
  reportReason: string;
  reportDetail?: string;
  targetType: 'USER' | 'MOVEMENT' | 'VIDEO' | 'COMMENT';
  targetId: number;
  targetContent?: string;
  reporterId: number;
  reporterNickname?: string;
  reportedUserId: number;
  reportedUserNickname: string;
  status: number;
  handlerId?: number;
  handlerNickname?: string;
  handleResult?: string;
  createdAt: string;
  handledAt?: string;
}

interface ReportQueryParams {
  page: number;
  size: number;
  reportType?: string;
  status?: number;
  targetType?: string;
}

const Reports: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<ReportRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [reportTypeFilter, setReportTypeFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<number | undefined>();
  const [targetTypeFilter, setTargetTypeFilter] = useState<string | undefined>();
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentReport, setCurrentReport] = useState<ReportRecord | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    fixed: 'left',
    columnWidth: 50,
  };

  const fetchData = useCallback(async (params: ReportQueryParams = { page, size: pageSize }) => {
    // 检查是否已登录
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，不请求数据');
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const payload = (await contentAdminApi.listReports(params)) as PageResult<ReportRecord>;
      setData(payload.records || payload.list || []);
      setTotal(payload.total || 0);
    } catch (error) {
      message.error((error as Error)?.message || '获取举报列表失败');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSearch = () => {
    setPage(1);
    fetchData({ 
      page: 1, 
      size: pageSize, 
      reportType: reportTypeFilter || undefined,
      status: statusFilter || undefined,
      targetType: targetTypeFilter || undefined,
    });
  };

  const handleReset = () => {
    setReportTypeFilter(undefined);
    setStatusFilter(undefined);
    setTargetTypeFilter(undefined);
    setPage(1);
    fetchData({ page: 1, size: pageSize });
  };

  const handleApprove = async (id: number) => {
    try {
      await contentAdminApi.handleReport(id);
      message.success('已处理举报');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const handleReject = async (id: number) => {
    try {
      await contentAdminApi.ignoreReport(id);
      message.success('已忽略举报');
      fetchData({ page, size: pageSize });
    } catch (error) {
      message.error((error as Error)?.message || '操作失败');
    }
  };

  const handleViewDetail = (record: ReportRecord) => {
    setCurrentReport(record);
    setDetailVisible(true);
  };

  const getStatusTag = (status: number) => {
    switch (status) {
      case 0:
        return <Tag color="orange">待处理</Tag>;
      case 1:
        return <Tag color="green">已处理</Tag>;
      case 2:
        return <Tag color="default">已忽略</Tag>;
      default:
        return <Tag>未知</Tag>;
    }
  };

  const getTargetTypeTag = (type: string) => {
    const typeMap: Record<string, { color: string; label: string }> = {
      USER: { color: 'blue', label: '用户' },
      MOVEMENT: { color: 'green', label: '动态' },
      VIDEO: { color: 'purple', label: '视频' },
      COMMENT: { color: 'orange', label: '评论' },
    };
    const config = typeMap[type] || { color: 'default', label: type };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const getReportTypeTag = (type: string) => {
    const colorMap: Record<string, string> = {
      '色情': 'red',
      '谩骂': 'orange',
      '诈骗': 'purple',
      '广告': 'blue',
      '其他': 'default',
    };
    return <Tag color={colorMap[type] || 'default'}>{type}</Tag>;
  };

  const columns: ColumnsType<ReportRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      fixed: 'left',
    },
    {
      title: '举报类型',
      dataIndex: 'reportType',
      key: 'reportType',
      width: 100,
      render: (type: string) => getReportTypeTag(type),
    },
    {
      title: '举报原因',
      dataIndex: 'reportReason',
      key: 'reportReason',
      width: 150,
      ellipsis: true,
    },
    {
      title: '目标类型',
      dataIndex: 'targetType',
      key: 'targetType',
      width: 100,
      render: (type: string) => getTargetTypeTag(type),
    },
    {
      title: '目标 ID',
      dataIndex: 'targetId',
      key: 'targetId',
      width: 100,
      render: (id: number) => <Text copyable>{id}</Text>,
    },
    {
      title: '被举报人',
      dataIndex: 'reportedUserNickname',
      key: 'reportedUserNickname',
      width: 120,
    },
    {
      title: '举报人',
      dataIndex: 'reporterNickname',
      key: 'reporterNickname',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => getStatusTag(status),
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
      width: 200,
      fixed: 'right',
      render: (_: unknown, record: ReportRecord) => (
        <Space size="small">
          <Button 
            type="link" 
            size="small" 
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            详情
          </Button>
          {record.status === 0 && (
            <>
              <Popconfirm
                title="确认处理"
                description="确定要处理这条举报吗？"
                onConfirm={() => handleApprove(record.id)}
                okText="确认"
                cancelText="取消"
              >
                <Button type="link" size="small" icon={<CheckOutlined />} style={{ color: '#52c41a' }}>
                  处理
                </Button>
              </Popconfirm>
              <Popconfirm
                title="确认忽略"
                description="确定要忽略这条举报吗？"
                onConfirm={() => handleReject(record.id)}
                okText="确认"
                cancelText="取消"
              >
                <Button type="link" size="small" icon={<CloseOutlined />} danger>
                  忽略
                </Button>
              </Popconfirm>
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb items={[
        { title: '内容审核' },
        { title: '举报管理' },
      ]} />
      
      <div style={{ marginBottom: '16px' }}>
        <Title level={4} style={{ margin: 0 }}>举报管理</Title>
      </div>

      <Card variant="borderless" styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px' }}>
          <Form layout="inline">
            <Form.Item label="举报类型">
              <Select
                placeholder="全部类型"
                value={reportTypeFilter}
                onChange={setReportTypeFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="色情">色情</Option>
                <Option value="谩骂">谩骂</Option>
                <Option value="诈骗">诈骗</Option>
                <Option value="广告">广告</Option>
                <Option value="其他">其他</Option>
              </Select>
            </Form.Item>
            <Form.Item label="目标类型">
              <Select
                placeholder="全部目标"
                value={targetTypeFilter}
                onChange={setTargetTypeFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value="USER">用户</Option>
                <Option value="MOVEMENT">动态</Option>
                <Option value="VIDEO">视频</Option>
                <Option value="COMMENT">评论</Option>
              </Select>
            </Form.Item>
            <Form.Item label="状态">
              <Select
                placeholder="全部状态"
                value={statusFilter}
                onChange={setStatusFilter}
                style={{ width: 120 }}
                allowClear
              >
                <Option value={0}>待处理</Option>
                <Option value={1}>已处理</Option>
                <Option value={2}>已忽略</Option>
              </Select>
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

        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          rowSelection={rowSelection}
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
          scroll={{ x: 'max-content' }}
          size="middle"
        />
      </Card>

      <Modal
        title="举报详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        {currentReport && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="举报 ID">{currentReport.id}</Descriptions.Item>
            <Descriptions.Item label="举报类型">{getReportTypeTag(currentReport.reportType)}</Descriptions.Item>
            <Descriptions.Item label="举报原因">{currentReport.reportReason}</Descriptions.Item>
            <Descriptions.Item label="详细描述">{currentReport.reportDetail || '-'}</Descriptions.Item>
            <Descriptions.Item label="目标类型">{getTargetTypeTag(currentReport.targetType)}</Descriptions.Item>
            <Descriptions.Item label="目标 ID">{currentReport.targetId}</Descriptions.Item>
            <Descriptions.Item label="目标内容">{currentReport.targetContent || '-'}</Descriptions.Item>
            <Descriptions.Item label="举报人">{currentReport.reporterNickname || `ID: ${currentReport.reporterId}`}</Descriptions.Item>
            <Descriptions.Item label="被举报人">{currentReport.reportedUserNickname}</Descriptions.Item>
            <Descriptions.Item label="状态">{getStatusTag(currentReport.status)}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{dayjs(currentReport.createdAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
            {currentReport.handledAt && (
              <>
                <Descriptions.Item label="处理人">{currentReport.handlerNickname || `ID: ${currentReport.handlerId}`}</Descriptions.Item>
                <Descriptions.Item label="处理时间">{dayjs(currentReport.handledAt).format('YYYY-MM-DD HH:mm:ss')}</Descriptions.Item>
                <Descriptions.Item label="处理结果">{currentReport.handleResult || '-'}</Descriptions.Item>
              </>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Reports;
