import { useState } from 'react';
import { Card, Typography, Empty } from 'antd';

const { Title } = Typography;

const Recommend: React.FC = () => {
  return (
    <div>
      <Title level={2}>推荐用户</Title>
      
      <Card>
        <Empty description="推荐用户页面开发中" />
      </Card>
    </div>
  );
};

export default Recommend;
