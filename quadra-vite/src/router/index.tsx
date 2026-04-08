import { createBrowserRouter, Navigate } from 'react-router-dom';
import AdminLayout from '@/layouts/AdminLayout';
import Login from '@/pages/Login';
import Dashboard from '@/pages/Dashboard';
import ProtectedRoute from '@/components/ProtectedRoute';

// 用户管理
import Users from '@/pages/users/Users';

// 内容审核
import Movements from '@/pages/content/Movements';
import Videos from '@/pages/content/Videos';
import Reports from '@/pages/content/Reports';

// 社交管理
import Matches from '@/pages/social/Matches';
import Friendships from '@/pages/social/Friendships';

// 互动管理
import Comments from '@/pages/interaction/Comments';

// 消息推送
import Notices from '@/pages/message/Notices';
import SendNotice from '@/pages/message/SendNotice';
import Templates from '@/pages/message/Templates';
import Announcements from '@/pages/message/Announcements';
import MessageRecords from '@/pages/message/Records';

// 日志监控
import OperationLogs from '@/pages/log/OperationLogs';
import LoginLogs from '@/pages/log/LoginLogs';
import ErrorLogs from '@/pages/log/ErrorLogs';
import ApiLogs from '@/pages/log/ApiLogs';
import SlowSQL from '@/pages/log/SlowSQL';
import RequestLogs from '@/pages/log/RequestLogs';

// 运维监控
import Services from '@/pages/monitor/Services';
import Performance from '@/pages/monitor/Performance';
import Alerts from '@/pages/monitor/Alerts';

// 系统管理
import Admins from '@/pages/system/Admins';
import Roles from '@/pages/system/Roles';
import Menus from '@/pages/system/Menus';
import Analysis from '@/pages/system/Analysis';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <AdminLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <Dashboard />,
      },
      {
        path: 'users',
        element: <Users />,
      },
      {
        path: 'content',
        children: [
          {
            path: 'movements',
            element: <Movements />,
          },
          {
            path: 'videos',
            element: <Videos />,
          },
          {
            path: 'reports',
            element: <Reports />,
          },
        ],
      },
      {
        path: 'social',
        children: [
          {
            path: 'matches',
            element: <Matches />,
          },
          {
            path: 'friendships',
            element: <Friendships />,
          },
        ],
      },
      {
        path: 'interaction',
        children: [
          {
            path: 'comments',
            element: <Comments />,
          },
        ],
      },
      {
        path: 'message',
        children: [
          {
            path: 'notices',
            element: <Notices />,
          },
          {
            path: 'send',
            element: <SendNotice />,
          },
          {
            path: 'templates',
            element: <Templates />,
          },
          {
            path: 'announcements',
            element: <Announcements />,
          },
          {
            path: 'records',
            element: <MessageRecords />,
          },
        ],
      },
      {
        path: 'log',
        children: [
          {
            path: 'operation',
            element: <OperationLogs />,
          },
          {
            path: 'login',
            element: <LoginLogs />,
          },
          {
            path: 'error',
            element: <ErrorLogs />,
          },
          {
            path: 'api',
            element: <ApiLogs />,
          },
          {
            path: 'slow-sql',
            element: <SlowSQL />,
          },
          {
            path: 'request',
            element: <RequestLogs />,
          },
        ],
      },
      {
        path: 'monitor',
        children: [
          {
            path: 'services',
            element: <Services />,
          },
          {
            path: 'performance',
            element: <Performance />,
          },
          {
            path: 'alerts',
            element: <Alerts />,
          },
        ],
      },
      {
        path: 'system/admins',
        element: <Admins />,
      },
      {
        path: 'system/roles',
        element: <Roles />,
      },
      {
        path: 'system/menus',
        element: <Menus />,
      },
      {
        path: 'system/analysis',
        element: <Analysis />,
      },
    ],
  },
]);

export default router;
