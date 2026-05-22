import axios from 'axios'
import { TOKEN_STORAGE_KEY } from '../features/auth/authStore'

export type PageResponse<T> = {
  items: T[]
}

export const httpClient = axios.create({
  baseURL: '/api',
})

httpClient.interceptors.request.use((config) => {
  const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
