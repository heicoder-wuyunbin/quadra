import { useState } from 'react';
import { Card, Typography, Empty } from 'antd';

const { Title } = Typography;

const Analysis: React.FC = () => {
  return (
    <div>
      <Title level={2}>数据分析</Title>
      
      <Card>
        <Empty description="数据分析页面开发中" />
      </Card>
    </div>
  );
};

export default Analysis;
