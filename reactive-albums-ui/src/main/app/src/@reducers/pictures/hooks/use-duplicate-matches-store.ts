import { EntityState } from '@reduxjs/toolkit';
import { DuplicateMatch } from '@types';
import { useSelector } from '@hooks';

export function useDuplicateMatchesStore(): EntityState<DuplicateMatch> {
  return useSelector((state) => state.pictures.duplicateMatches);
}
