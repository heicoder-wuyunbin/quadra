import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:18080',
        changeOrigin: true,
        // 前端统一 /api/**，但网关/后端实际不带 /api：
        //   /api/system/** -> /system/**
        rewrite: (p) => p.replace(/^\/api/, ''),
      },
    },
  },
})
