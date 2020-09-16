import { useEventSource } from '@hooks';
import { useCallback, useState } from 'react';

export function useTotalDuplicateCount(): number {
  const [duplicateMatchCount, setDuplicateMatchCount] = useState(0);
  const msgHandler = useCallback((msg: string) => setDuplicateMatchCount(+msg), [setDuplicateMatchCount]);

  useEventSource('/events/duplicate-match-count', {}, [], msgHandler);

  return duplicateMatchCount;
}
