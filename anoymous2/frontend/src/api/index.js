// src/api.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,  // 쿠키 인증시 필요
});

export default apiClient;
