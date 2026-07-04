import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// El frontend consume el API Gateway (Spring Cloud Gateway) en :8080.
// En desarrollo, /api se proxea al gateway para evitar problemas de CORS.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
