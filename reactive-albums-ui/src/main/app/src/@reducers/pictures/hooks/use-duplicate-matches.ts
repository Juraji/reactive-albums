import { useApiUrl, useEventSource } from '@hooks';
import { useCallback, useState } from 'react';
import { DuplicateMatch, ReactiveEvent } from '@types';

export function useDuplicateMatches(): DuplicateMatch[] {
  const [matches, setMatches] = useState<DuplicateMatch[]>([]);

  const esEndpoint = useApiUrl('events', 'duplicate-matches');
  const msgHandler = useCallback(
    (msg: string) => {
      const evt: ReactiveEvent<DuplicateMatch> = JSON.parse(msg);

      switch (evt.type) {
        case 'UPSERT':
          setMatches((prev) => [...prev, evt.entity]);
          break;
        case 'DELETE':
          setMatches((prev) => prev.filter((dm) => dm.id !== evt.entity.id));
          break;
      }
    },
    [setMatches]
  );

  useEventSource(msgHandler, esEndpoint);

  return matches;
}

export function useTotalDuplicateCount(): number {
  const [duplicateMatchCount, setDuplicateMatchCount] = useState(0);
  const esEndpoint = useApiUrl('events', 'duplicate-match-count');
  const msgHandler = useCallback((msg: string) => setDuplicateMatchCount(+msg), [setDuplicateMatchCount]);

  useEventSource(msgHandler, esEndpoint);

  return duplicateMatchCount;
}
