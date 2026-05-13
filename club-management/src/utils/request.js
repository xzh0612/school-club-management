import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加默认的Content-Type
    if (!config.headers['Content-Type'] && !config.headers['content-type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    
    // 添加 token
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      const errorMsg = res.msg || res.message || '请求失败'
      console.error('API错误:', errorMsg)
      ElMessage.error(errorMsg)
      
      // 401: 未授权，跳转到登录页
      if (res.code === 401) {
        sessionStorage.removeItem('user')
        sessionStorage.removeItem('token')
        window.dispatchEvent(new Event('auth:expired'))
      }
      
      return Promise.reject(new Error(errorMsg))
    }
    
    return res
  },
  error => {
    console.error('HTTP 错误:', error)
    console.error('错误响应:', error.response?.data)
    const errorMsg = error.response?.data?.msg || error.response?.data?.message || error.message || '网络错误，请稍后重试'
    ElMessage.error(errorMsg)
    if (error.response?.status === 401) {
      sessionStorage.removeItem('user')
      sessionStorage.removeItem('token')
      window.dispatchEvent(new Event('auth:expired'))
    }
    return Promise.reject(error)
  }
)

export default request
