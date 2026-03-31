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
    key: '/user',
    icon: <UserOutlined />,
    label: '用户管理',
    children: [
      {
        key: '/user/users',
        label: '用户列表',
      },
      {
        key: '/user/audit',
        label: '用户审核',
      },
      {
        key: '/user/blacklist',
        label: '黑名单管理',
      },
    ],
  },
  {
    key: '/content',
    icon: <FileTextOutlined />,
    label: '内容审核',
    children: [
      {
        key: '/content/movements',
        label: '动态审核',
      },
      {
        key: '/content/videos',
        label: '视频审核',
      },
      {
        key: '/content/reports',
        label: '举报管理',
      },
    ],
  },
  {
    key: '/social',
    icon: <TeamOutlined />,
    label: '社交管理',
    children: [
      {
        key: '/social/matches',
        label: '匹配记录',
      },
      {
        key: '/social/friendships',
        label: '好友关系',
      },
    ],
  },
  {
    key: '/interaction',
    icon: <MessageOutlined />,
    label: '互动管理',
    children: [
      {
        key: '/interaction/comments',
        label: '评论管理',
      },
    ],
  },
  {
    key: '/message',
    icon: <BellOutlined />,
    label: '消息推送',
    children: [
      {
        key: '/message/notices',
        label: '站内信管理',
      },
      {
        key: '/message/send',
        label: '发送站内信',
      },
      {
        key: '/message/templates',
        label: '消息模板',
      },
      {
        key: '/message/announcements',
        label: '系统公告',
      },
      {
        key: '/message/records',
        label: '推送记录',
      },
    ],
  },
  {
    key: '/log',
    icon: <FileDoneOutlined />,
    label: '日志监控',
    children: [
      {
        key: '/log/operation',
        label: '操作日志',
      },
      {
        key: '/log/login',
        label: '登录日志',
      },
      {
        key: '/log/error',
        label: '错误日志',
      },
      {
        key: '/log/api',
        label: '接口日志',
      },
      {
        key: '/log/slow-sql',
        label: '慢查询日志',
      },
    ],
  },
  {
    key: '/monitor',
    icon: <MonitorOutlined />,
    label: '运维监控',
    children: [
      {
        key: '/monitor/services',
        label: '服务监控',
      },
      {
        key: '/monitor/performance',
        label: '性能监控',
      },
      {
        key: '/monitor/alerts',
        label: '告警管理',
      },
    ],
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
