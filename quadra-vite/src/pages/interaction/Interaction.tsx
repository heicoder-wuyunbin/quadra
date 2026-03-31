import { useState } from 'react';
import { Card, Typography, Empty } from 'antd';

const { Title } = Typography;

const Interaction: React.FC = () => {
  return (
    <div>
      <Title level={2}>互动管理</Title>
      
      <Card>
        <Empty description="互动管理页面开发中" />
      </Card>
    </div>
  );
};

export default Interaction;
