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
  const cleanUpCallback = useCallback(() => eventSourceRef.current?.close(), [eventSourceRef]);

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
      es.onmessage = (e) => onEvent(e.data);
      es.onerror = (e) => console.error('[useEventSource] SSE error', e);

      if (isDevelopmentEnv()) {
        es.onopen = (e: Event) => console.log('[useEventSource] SSE opened', e);
      }

      eventSourceRef.current = es;
    }

    return cleanUpCallback;
  }, [authorizedEndpoint, canEnable, eventSourceRef, cleanUpCallback, onEvent]);
}
