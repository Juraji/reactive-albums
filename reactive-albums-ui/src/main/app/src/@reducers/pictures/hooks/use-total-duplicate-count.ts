import { useMemo } from 'react';
import { useDuplicateMatchesStore } from './use-duplicate-matches-store';
import { selectTotalDuplicateMatches } from '../duplicates-store.slice';

export function useTotalDuplicateCount(): number {
  const duplicateMatchesStore = useDuplicateMatchesStore();
  return useMemo(() => selectTotalDuplicateMatches(duplicateMatchesStore), [duplicateMatchesStore]);
}
