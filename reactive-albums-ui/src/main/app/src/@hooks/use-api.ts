import { useMemo } from 'react';

export function useApiUrl(...path: string[]): string {
  return useMemo(() => {
    const base = `${window.origin}/api`;

    if (!!path) {
      return `${base}/${path.join('/')}`;
    } else {
      return base;
    }
  }, [path]);
}
