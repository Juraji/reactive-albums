import { useHistory, useLocation } from 'react-router-dom';
import { useCallback, useMemo } from 'react';

interface QuerParams {
  get(name: string): string | undefined;

  set(name: string, value: any): void;

  unset(name: string): void;
}

export function useQueryParams(): QuerParams {
  const location = useLocation();
  const history = useHistory();

  const urlSearchParams = useMemo(() => {
    return location.search
      .replace('?', '')
      .split('&')
      .map((pair) => pair.split('='))
      .reduce((acc, next) => {
        acc.set(next[0], next[1] !== undefined ? next[1] : 'true');
        return acc;
      }, new URLSearchParams());
  }, [location]);

  const getCallback = useCallback(
    (name: string): string | undefined => {
      return urlSearchParams.get(name) || undefined;
    },
    [urlSearchParams]
  );

  const setCallback = useCallback(
    (name: string, value: string) => {
      if (urlSearchParams.get(name) !== value) {
        const newParams = new URLSearchParams(urlSearchParams);
        newParams.set(name, value);
        history.push({ search: `?${newParams.toString()}` });
      }
    },
    [urlSearchParams, history]
  );

  const unsetCallback = useCallback(
    (name: string) => {
      if (urlSearchParams.has(name)) {
        const newParams = new URLSearchParams(urlSearchParams);
        newParams.delete(name);
        history.push({ search: `?${newParams.toString()}` });
      }
    },
    [urlSearchParams, history]
  );

  return useMemo(
    () => ({
      get: getCallback,
      set: setCallback,
      unset: unsetCallback,
    }),
    [getCallback, setCallback, unsetCallback]
  );
}
