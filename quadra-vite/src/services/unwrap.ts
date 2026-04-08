import type { AxiosResponse } from 'axios';
import type { ApiResult } from './types';

export class ApiBizError extends Error {
  code?: number;
  requestId?: string;
  constructor(message: string, code?: number, requestId?: string) {
    super(message);
    this.name = 'ApiBizError';
    this.code = code;
    this.requestId = requestId;
  }
}

/**
 * 统一处理后端返回结构：
 * - 标准结构：AxiosResponse<ApiResult<T>> 或 ApiResult<T>
 * - 兼容结构：AxiosResponse<T> 或直接 T
 */
export function unwrapApiData<T>(
  input: AxiosResponse<ApiResult<T> | T> | ApiResult<T> | T
): T {
  const unknownInput: unknown = input;
  const body =
    typeof unknownInput === 'object' &&
    unknownInput !== null &&
    'data' in unknownInput
      ? (unknownInput as { data: unknown }).data
      : unknownInput;

  if (
    body &&
    typeof body === 'object' &&
    'success' in body &&
    'data' in body
  ) {
    const result = body as ApiResult<T>;
    if (!result.success) {
      throw new ApiBizError(result.message || '请求失败', result.code, result.requestId);
    }
    return result.data;
  }
  return body as T;
}
