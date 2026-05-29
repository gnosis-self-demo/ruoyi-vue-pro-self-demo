import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3004,
    proxy: {
      '/accounts': {
        target: 'http://localhost:8087',
        changeOrigin: true,
      }
    }
  }
})
