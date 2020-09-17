import { DependencyList, useCallback, useEffect, useMemo, useRef } from 'react';
import { isDevelopmentEnv } from '@utils';

export function useEventSource(onEvent: (msg: string) => void, endpoint: string, deps: DependencyList = []) {
  const eventSourceRef = useRef<EventSource>();
  const canEnable = useMemo(() => !deps.some((d) => !d), [deps]);
  const cleanUpCallback = useCallback(() => {
    const es = eventSourceRef.current;
    if (!!es && es.readyState === EventSource.OPEN) {
      es.close();
      es.dispatchEvent(new Event('close'));
    }
  }, [eventSourceRef]);

  useEffect(() => {
    if (!!eventSourceRef.current) {
      cleanUpCallback();
    }

    if (canEnable) {
      const es = new EventSource(endpoint);
      es.addEventListener('message', (e) => onEvent(e.data));
      es.addEventListener('error', (e) => console.error(`[useEventSource(${endpoint})] SSE error`, e));

      if (isDevelopmentEnv()) {
        es.addEventListener('open', (e: Event) => console.log(`[useEventSource(${endpoint})] SSE opened`, e));
        es.addEventListener('close', (e: Event) => console.log(`[useEventSource(${endpoint})] SSE closed`, e));
      }

      eventSourceRef.current = es;
    }

    return cleanUpCallback;
  }, [endpoint, canEnable, eventSourceRef, cleanUpCallback, onEvent]);
}
