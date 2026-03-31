import { ConfigProvider, theme } from 'antd'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './layouts/AdminLayout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import ContentManagement from './pages/content/Management'
import UserManagement from './pages/user/Management'
import SystemManagement from './pages/system/Management'

function App() {
  return (
    <ConfigProvider
      theme={{
        algorithm: theme.defaultAlgorithm,
        token: {
          colorPrimary: '#1677ff',
          borderRadius: 6,
        },
      }}
    >
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Layout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="content" element={<ContentManagement />} />
            <Route path="users" element={<UserManagement />} />
            <Route path="system" element={<SystemManagement />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  )
}

export default App
