import { useDuplicateMatchesStore } from './use-duplicate-matches-store';
import { useMemo } from 'react';
import { DuplicateMatch } from '@types';
import { selectAllDuplicateMatches } from '../duplicates-store.slice';

interface MatchGroups {
  sourcePictureIds: string[];
  matchGroups: Record<string, DuplicateMatch[]>;
}

export function useGroupedDuplicateMatches(): MatchGroups {
  const duplicateMatchesStore = useDuplicateMatchesStore();

  return useMemo(() => {
    const matchGroups = selectAllDuplicateMatches(duplicateMatchesStore).partition((it) => it.pictureId);
    const sourcePictureIds = Object.keys(matchGroups);

    return {
      sourcePictureIds,
      matchGroups,
    };
  }, [duplicateMatchesStore]);
}
