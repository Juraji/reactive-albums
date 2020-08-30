import { AxiosResponse } from 'axios';

interface ApiErrorResult {
  message: string;
  status: string;
}

export function unwrapApiResponse<R>(response: AxiosResponse<R | ApiErrorResult>): R {
  const data: R | ApiErrorResult = response.data;

  if ('message' in data && 'status' in data) {
    throw new Error(`[${data.status}] ${data.message}`);
  } else {
    return data;
  }
}
