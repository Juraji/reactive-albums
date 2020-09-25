import { AxiosError, AxiosRequestConfig, AxiosResponse, default as axios, Method } from 'axios';
import { createAsyncThunk } from '@reduxjs/toolkit';
import { AppState } from '@reducers';

interface ApiErrorResult {
  message: string;
  status: string;
}

interface ThunkAPI {
  getState(): AppState;

  dispatch(): void;
}

type RequestCreator<P> = (payload: P, api: ThunkAPI) => string | (AxiosRequestConfig & { url: string });

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

function runRequest<R, P>(method: Method, creator: RequestCreator<P>, p: P, t: ThunkAPI): Promise<R> {
  const created = creator(p, t);
  const conf = typeof created === 'string' ? { method, url: created } : created.copy({ method });
  return axios.request(conf).then((r) => r.data);
}

export function createApiGetThunk<R, P = void>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p, ta: any) => runRequest('GET', requestCreator, p, ta));
}

export function createApiPostThunk<R, P = never>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p, ta: any) => runRequest('POST', requestCreator, p, ta));
}

export function createApiPutThunk<R, P = never>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p, ta: any) => runRequest('PUT', requestCreator, p, ta));
}

export function createApiDeleteThunk<R, P = never>(typePrefix: string, requestCreator: RequestCreator<P>) {
  return createAsyncThunk<R, P>(typePrefix, (p, ta: any) => runRequest('DELETE', requestCreator, p, ta));
}
