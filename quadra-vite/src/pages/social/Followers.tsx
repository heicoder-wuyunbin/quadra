import { useState } from 'react';
import { Card, Typography, Empty } from 'antd';

const { Title } = Typography;

const Followers: React.FC = () => {
  return (
    <div>
      <Title level={2}>粉丝管理</Title>
      
      <Card>
        <Empty description="粉丝管理页面开发中" />
      </Card>
    </div>
  );
};

export default Followers;
