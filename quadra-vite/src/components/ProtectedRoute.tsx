import { Navigate, useLocation } from 'react-router-dom';
import { isAdminLoggedIn } from '@/utils/storage';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const location = useLocation();
  const isLoggedIn = isAdminLoggedIn();

  if (!isLoggedIn) {
    // 如果未登录，重定向到登录页，并记录当前路径
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
