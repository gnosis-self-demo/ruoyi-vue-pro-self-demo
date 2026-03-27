import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3005,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
})