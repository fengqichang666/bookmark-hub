import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    // localStorageMock 必须排在 setup.ts 之前：setup.ts 会 import authStore，
    // 而后者在模块加载时就读取 localStorage。
    setupFiles: ['./src/test/localStorageMock.ts', './src/test/setup.ts'],
  },
})
