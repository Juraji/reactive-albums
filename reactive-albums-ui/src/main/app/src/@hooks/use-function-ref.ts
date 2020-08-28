import { MutableRefObject, useEffect, useRef } from 'react';

export function useFunctionRef<T extends (...args: any[]) => any>(fn: T): MutableRefObject<T> {
  const fnRef = useRef(fn);

  useEffect(() => (fnRef.current = fn), [fnRef, fn]);

  return fnRef;
}
