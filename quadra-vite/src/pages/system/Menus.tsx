import { Card, Typography, Empty } from 'antd';

const { Title } = Typography;

const Menus: React.FC = () => {
  return (
    <div>
      <Title level={2}>菜单管理</Title>
      
      <Card>
        <Empty description="菜单管理页面开发中" />
      </Card>
    </div>
  );
};

export default Menus;
