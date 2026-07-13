import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 3008,
    proxy: {
      '/signature': {
        target: 'http://localhost:8095',
        changeOrigin: true,
      }
    }
  }
})
