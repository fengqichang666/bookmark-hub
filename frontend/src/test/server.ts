import { HttpResponse, type StrictResponse } from 'msw'
import { setupServer } from 'msw/node'

export const server = setupServer()

/**
 * 后端所有接口都返回统一响应体 { code, message, data }，
 * mock 时用这个包一层，保持和真实响应一致（httpClient 会自动拆包）。
 */
export function jsonResult<T>(data: T, init?: ResponseInit): StrictResponse<never> {
  return HttpResponse.json(
    { code: 0, message: 'OK', data },
    init,
  ) as unknown as StrictResponse<never>
}
