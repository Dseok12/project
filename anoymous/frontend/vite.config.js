import vue from '@vitejs/plugin-vue';
import path from 'path';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@css': path.resolve(__dirname, './src/css'),
      '@cssBase': path.resolve(__dirname, './src/css/base'),
      '@cssCommon': path.resolve(__dirname, './src/css/common'),
      '@cssComponent': path.resolve(__dirname, './src/css/component'),
      '@cssLayout': path.resolve(__dirname, './src/css/layout'),
      '@cssPage': path.resolve(__dirname, './src/css/page'),
      '@cssMixin': path.resolve(__dirname, './src/css/mixin'),
      '@cssEvent': path.resolve(__dirname, './src/css/event'),
      '@cssTeacher': path.resolve(__dirname, './src/css/teacher'),
      '@cssServices': path.resolve(__dirname, './src/css/services'),
      '@cssAdmin': path.resolve(__dirname, './src/css/admin'),
      '@cssMypage': path.resolve(__dirname, './src/css/mypage'),

      // vue
      '@components': path.resolve(__dirname, './src/jsx/components'),
      '@layouts': path.resolve(__dirname, './src/jsx/layouts'),
      '@pages': path.resolve(__dirname, './src/jsx/pages'),
      '@hooks': path.resolve(__dirname, './src/hooks'),
      '@store': path.resolve(__dirname, './src/store'),
      '@utils': path.resolve(__dirname, './src/utils'),

      // api
      '@api': path.resolve(__dirname, './src/api'),
    },
  },
  // css: {
  //   preprocessorOptions: {
  //     scss: {
  //       additionalData: `
  //         @use "@cssBase/reset" as *;
  //         @use "@cssBase/common" as *;
  //       `
  //     }
  //   }
  // },  
  server: {
    port: 3000, // ✅ 여기서 포트번호 지정
  },
});