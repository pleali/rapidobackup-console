import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src/main/resources/webapp/src"),
    },
  },
  root: "./src/main/resources/webapp",
  publicDir: "./public",
  build: {
    outDir: "../../../../target/classes/static",
    rollupOptions: {
      input: {
        main: path.resolve(__dirname, 'src/main/resources/webapp/index.html')
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