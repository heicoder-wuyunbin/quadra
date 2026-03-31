import { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Table, Typography, DatePicker, Spin } from 'antd';
import {
  UserAddOutlined,
  UserOutlined,
  FileAddOutlined,
  MessageOutlined,
  HeartOutlined,
} from '@ant-design/icons';
import { adminApi } from '@/services/api';
import type { DailyAnalysisDTO } from '@/services/types';
import dayjs from 'dayjs';

const { Title } = Typography;

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [analysisData, setAnalysisData] = useState<DailyAnalysisDTO | null>(null);
  const [selectedDate, setSelectedDate] = useState(dayjs());

  useEffect(() => {
    fetchAnalysisData();
  }, [selectedDate]);

  const fetchAnalysisData = async () => {
    setLoading(true);
    try {
      const date = selectedDate.format('YYYY-MM-DD');
      const res = await adminApi.getDailyAnalysis(date);
      setAnalysisData(res.data);
    } catch (error) {
      // 401 错误不显示 message，只记录日志
      if ((error as any)?.response?.status !== 401) {
        console.error('Failed to fetch analysis data:', error);
      }
      setAnalysisData(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Title level={2}>仪表盘</Title>
      
      <Card style={{ marginBottom: 24 }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Title level={4} style={{ margin: 0 }}>选择日期</Title>
          </Col>
          <Col>
            <DatePicker 
              value={selectedDate}
              onChange={(date) => date && setSelectedDate(date)}
              showToday
            />
          </Col>
        </Row>
      </Card>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <Spin size="large" />
        </div>
      ) : analysisData ? (
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={4}>
            <Card>
              <Statistic
                title="新增用户"
                value={analysisData.newUsers}
                prefix={<UserAddOutlined />}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={8} lg={4}>
            <Card>
              <Statistic
                title="活跃用户"
                value={analysisData.activeUsers}
                prefix={<UserOutlined />}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={8} lg={4}>
            <Card>
              <Statistic
                title="新增内容"
                value={analysisData.newContents}
                prefix={<FileAddOutlined />}
                valueStyle={{ color: '#722ed1' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={8} lg={4}>
            <Card>
              <Statistic
                title="互动次数"
                value={analysisData.interactions}
                prefix={<MessageOutlined />}
                valueStyle={{ color: '#fa8c16' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={8} lg={4}>
            <Card>
              <Statistic
                title="匹配次数"
                value={analysisData.matches}
                prefix={<HeartOutlined />}
                valueStyle={{ color: '#eb2f96' }}
              />
            </Card>
          </Col>
        </Row>
      ) : (
        <Card>
          <Typography.Text type="secondary">暂无数据</Typography.Text>
        </Card>
      )}
    </div>
  );
};

export default Dashboard;
