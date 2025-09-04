import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src/main/webapp/src"),
    },
  },
  root: path.resolve(__dirname, "src/main/webapp"),
  publicDir: "public",
  cacheDir: path.resolve(__dirname, 'target/.vite-cache'),

  build: {
    outDir: path.resolve(__dirname, "target/classes/static"),
    rollupOptions: {
      input: {
        main: path.resolve(__dirname, 'src/main/webapp/index.html')
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})