import { useEffect } from 'react';

/**
 * 在未登录时阻止数据加载
 * @param fetchData 数据获取函数
 * @param dependencies 依赖数组
 */
export const useFetchWithAuth = (
  fetchData: () => Promise<void>,
  dependencies: unknown[] = []
) => {
  useEffect(() => {
    // 检查是否已登录
    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
      console.log('未登录，跳过数据加载');
      return;
    }

    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencies);
};
