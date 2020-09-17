import { DependencyList, useCallback, useEffect, useMemo, useRef } from 'react';
import { isDevelopmentEnv } from '@utils';

export function useEventSource(
  endpoint: string,
  params: Record<string, string> = {},
  deps: DependencyList = [],
  onEvent: (msg: string) => void
) {
  const eventSourceRef = useRef<EventSource>();
  const canEnable = useMemo(() => !deps.some((d) => !d), [deps]);
  const cleanUpCallback = useCallback(() => {
    const es = eventSourceRef.current;
    if (!!es) {
      es.close();
      es.dispatchEvent(new Event('close'));
    }
  }, [eventSourceRef]);

  const authorizedEndpoint = useMemo(() => {
    const q = new URLSearchParams(params);
    return `${window.origin}/api${endpoint}?${q.toString()}`;
  }, [endpoint, params]);

  useEffect(() => {
    if (!!eventSourceRef.current) {
      cleanUpCallback();
    }

    if (canEnable) {
      const es = new EventSource(authorizedEndpoint);
      es.addEventListener('message', (e) => onEvent(e.data));
      es.addEventListener('error', (e) => console.error(`[useEventSource(${authorizedEndpoint})] SSE error`, e));

      if (isDevelopmentEnv()) {
        es.addEventListener('open', (e: Event) => console.log(`[useEventSource(${authorizedEndpoint})] SSE opened`, e));
        es.addEventListener('close', (e: Event) =>
          console.log(`[useEventSource(${authorizedEndpoint})] SSE closed`, e)
        );
      }

      eventSourceRef.current = es;
    }

    return cleanUpCallback;
  }, [authorizedEndpoint, canEnable, eventSourceRef, cleanUpCallback, onEvent]);
}
