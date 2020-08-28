import axios, { AxiosInstance } from 'axios';
import { useMemo } from 'react';

export function useApiUrl(...path: string[]): string {
  return useMemo(() => {
    const base = `${window.origin}/api`;

    if (!!path) {
      return `${base}/${path.reduce((acc, next) => `${acc}/${next}`)}`;
    } else {
      return base;
    }
  }, [path]);
}

export function useApi(): AxiosInstance {
  const baseURL = useApiUrl();
  return useMemo(() => axios.create({ baseURL }), [baseURL]);
}
