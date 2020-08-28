import { DependencyList, useCallback, useEffect, useMemo, useRef } from 'react';
import { useFunctionRef } from './use-function-ref';

export function useEventSource(
  endpoint: string,
  params: Record<string, string> = {},
  deps: DependencyList = [],
  onEvent: (msg: string) => void
) {
  const eventSourceRef = useRef<EventSource>();
  const eventHandlerRef = useFunctionRef(onEvent);
  const canEnable = useMemo(() => !deps.some((d) => !d), [deps]);
  const cleanUpCallback = useCallback(() => eventSourceRef.current?.close(), [eventSourceRef]);

  const authorizedEndpoint = useMemo(() => {
    const qParams = new URLSearchParams(params);
    return `${window.origin}/api${endpoint}?${qParams.toString()}`;
  }, [endpoint, params]);

  useEffect(() => {
    if (!!eventSourceRef.current) {
      cleanUpCallback();
    }

    if (canEnable) {
      const es = new EventSource(authorizedEndpoint);
      es.onmessage = (e) => eventHandlerRef.current(e.data);
      es.onerror = console.error;
      es.onopen = console.log;

      eventSourceRef.current = es;
    }

    return cleanUpCallback;
  }, [authorizedEndpoint, canEnable, eventSourceRef, cleanUpCallback, eventHandlerRef]);
}
