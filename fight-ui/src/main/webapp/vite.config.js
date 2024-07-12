import { defineConfig } from 'vite'
import preact from '@preact/preset-vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [preact()],
  build: {
    target: 'esnext'
  },
  server: {
    proxy: {
      '^/gateway/.*': 'http://localhost:8080'
    }
  }
})
