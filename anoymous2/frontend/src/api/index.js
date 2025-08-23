import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8081',
  withCredentials: true // 인증 쿠키를 사용할 경우
});

export default apiClient;
