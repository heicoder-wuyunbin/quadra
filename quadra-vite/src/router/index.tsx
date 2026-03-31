import { createBrowserRouter, Navigate } from 'react-router-dom';
import AdminLayout from '@/layouts/AdminLayout';
import Login from '@/pages/Login';
import Dashboard from '@/pages/Dashboard';
import Users from '@/pages/users/Users';
import Timeline from '@/pages/content/Timeline';
import Followers from '@/pages/social/Followers';
import Recommend from '@/pages/social/Recommend';
import Interaction from '@/pages/interaction/Interaction';
import Admins from '@/pages/system/Admins';
import Roles from '@/pages/system/Roles';
import Menus from '@/pages/system/Menus';
import Analysis from '@/pages/system/Analysis';
import { isAdminLoggedIn } from '@/utils/storage';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: <AdminLayout />,
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
        path: 'content/timeline',
        element: <Timeline />,
      },
      {
        path: 'social/followers',
        element: <Followers />,
      },
      {
        path: 'social/recommend',
        element: <Recommend />,
      },
      {
        path: 'interaction',
        element: <Interaction />,
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
