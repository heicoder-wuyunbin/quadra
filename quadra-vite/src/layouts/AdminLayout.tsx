import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, theme } from 'antd';
import type { MenuProps } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  FileTextOutlined,
  TeamOutlined,
  MessageOutlined,
  BellOutlined,
  FileDoneOutlined,
  MonitorOutlined,
  SettingOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { adminApi } from '@/services/api';
import { removeToken } from '@/utils/storage';

const { Header, Sider, Content } = Layout;

const menuItems: MenuProps['items'] = [
  {
    key: '/',
    icon: <DashboardOutlined />,
    label: '仪表盘',
  },
  {
    key: '/users',
    icon: <UserOutlined />,
    label: '用户管理',
  },
  {
    key: '/content',
    icon: <FileTextOutlined />,
    label: '内容管理',
    children: [
      {
        key: '/content/timeline',
        label: '时间线',
      },
    ],
  },
  {
    key: '/social',
    icon: <TeamOutlined />,
    label: '社交管理',
    children: [
      {
        key: '/social/followers',
        label: '粉丝管理',
      },
      {
        key: '/social/recommend',
        label: '推荐用户',
      },
    ],
  },
  {
    key: '/interaction',
    icon: <MessageOutlined />,
    label: '互动管理',
  },
  {
    key: '/system',
    icon: <SettingOutlined />,
    label: '系统管理',
    children: [
      {
        key: '/system/admins',
        label: '管理员管理',
      },
      {
        key: '/system/roles',
        label: '角色管理',
      },
      {
        key: '/system/menus',
        label: '菜单管理',
      },
      {
        key: '/system/analysis',
        label: '数据分析',
      },
    ],
  },
];

const AdminLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const handleMenuClick: MenuProps['onClick'] = (e) => {
    navigate(e.key);
  };

  const handleLogout = async () => {
    try {
      await adminApi.logout();
    } finally {
      removeToken();
      navigate('/login');
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
        <div style={{ 
          height: 32, 
          margin: 16, 
          background: 'rgba(255, 255, 255, 0.2)',
          borderRadius: 6,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          fontWeight: 'bold',
        }}>
          {collapsed ? 'Q' : 'Quadra Admin'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: colorBgContainer, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
          <Menu
            mode="horizontal"
            style={{ flex: 1, minWidth: 0, justifyContent: 'flex-end' }}
            items={[
              {
                key: 'logout',
                icon: <LogoutOutlined />,
                label: '退出登录',
                onClick: handleLogout,
              },
            ]}
          />
        </Header>
        <Content
          style={{
            margin: 16,
            padding: 24,
            background: colorBgContainer,
            borderRadius: borderRadiusLG,
            minHeight: 280,
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default AdminLayout;
