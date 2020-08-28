import { DependencyList, useCallback } from 'react';

function debounce<P extends any[], F extends (...args: P) => void>(fn: F, wait: number): F {
  let tid: number | undefined;

  return ((...args: P): void => {
    window.clearTimeout(tid);
    tid = window.setTimeout(() => fn(...args), wait);
  }) as F;
}

export function useDebounce<F extends (...args: any[]) => void>(fn: F, wait: number, dependencies: DependencyList): F {
  const partial = debounce(fn, wait);
  return useCallback(partial, dependencies);
}
