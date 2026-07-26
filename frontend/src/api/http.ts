import axios from 'axios'
import { TOKEN_STORAGE_KEY } from '../features/auth/authStore'

export type PageResponse<T> = {
  items: T[]
}

export type PageResult<T> = {
  items: T[]
  total: number
  page: number
  size: number
}

/** 后端统一响应体，code === 0 表示成功。 */
export type ApiResult<T> = {
  code: number
  message: string
  data: T
}

/** 业务失败（HTTP 2xx 但 code !== 0）时抛出，便于调用方统一 catch。 */
export class ApiError extends Error {
  readonly code: number

  constructor(code: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.code = code
  }
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

// 后端返回 { code, message, data }，这里统一拆包成 data，
// 各调用点仍按裸 payload 使用，无需逐个改造。
httpClient.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult<unknown> | undefined
    if (body && typeof body === 'object' && 'code' in body && 'data' in body) {
      if (body.code !== 0) {
        throw new ApiError(body.code, body.message)
      }
      response.data = body.data
    }
    return response
  },
  (error) => {
    // 失败响应同样是 Result 结构，把后端的 message 提上来替代 axios 的通用文案
    const body = error?.response?.data
    if (body && typeof body === 'object' && 'code' in body && 'message' in body) {
      return Promise.reject(new ApiError(body.code, body.message))
    }
    return Promise.reject(error)
  },
)
