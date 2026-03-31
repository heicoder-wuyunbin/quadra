import { useState } from 'react';
import { Card, Typography, Empty } from 'antd';

const { Title } = Typography;

const Roles: React.FC = () => {
  return (
    <div>
      <Title level={2}>角色管理</Title>
      
      <Card>
        <Empty description="角色管理页面开发中" />
      </Card>
    </div>
  );
};

export default Roles;
