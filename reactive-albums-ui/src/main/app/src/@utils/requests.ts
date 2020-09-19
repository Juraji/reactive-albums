import { AxiosError, AxiosRequestConfig, AxiosResponse, default as axios, Method } from 'axios';
import { createAsyncThunk } from '@reduxjs/toolkit';

interface ApiErrorResult {
  message: string;
  status: string;
}

type RequestCreator<P> = (p: P) => string | (AxiosRequestConfig & { url: string });

export function installAxiosInterceptors() {
  axios.interceptors.response.use(
    (response: AxiosResponse) => response,
    (error: AxiosError<ApiErrorResult>) => {
      if (!!error.response?.data.message) {
        return Promise.reject(`${error.response.data.message} (${error.response.data.status})`);
      } else {
        return Promise.reject(error);
      }
    }
  );
}

function runRequest<R, P>(method: Method, creator: RequestCreator<P>, p: P): Promise<R> {
  const created = creator(p);
  const conf = typeof created === 'string' ? { method, url: created } : created.copy({ method });
  return axios.request(conf).then((r) => r.data);
}

export function createApiGetThunk<R, P = void>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p) => runRequest('GET', requestCreator, p));
}

export function createApiPostThunk<R, P = never>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p) => runRequest('POST', requestCreator, p));
}

export function createApiPutThunk<R, P = never>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p) => runRequest('PUT', requestCreator, p));
}

export function createApiDeleteThunk<R, P = never>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p) => runRequest('DELETE', requestCreator, p));
}
