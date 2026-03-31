export const setToken = (accessToken: string, refreshToken: string): void => {
  localStorage.setItem('access_token', accessToken);
  localStorage.setItem('refresh_token', refreshToken);
};

export const getToken = (): { accessToken: string | null; refreshToken: string | null } => {
  return {
    accessToken: localStorage.getItem('access_token'),
    refreshToken: localStorage.getItem('refresh_token'),
  };
};

export const removeToken = (): void => {
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
};

export const isAdminLoggedIn = (): boolean => {
  return !!localStorage.getItem('access_token');
};
