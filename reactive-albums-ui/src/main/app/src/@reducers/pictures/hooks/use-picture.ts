import { DuplicateMatch, Picture } from '@types';
import { usePicturesStore } from './use-pictures-store';
import { useDuplicateMatchesStore } from './use-duplicate-matches-store';
import { useMemo } from 'react';
import { selectPictureById } from '../picture-store.slice';
import { selectAllDuplicateMatches } from '../duplicates-store.slice';

export function usePicture(pictureId: string): Picture | undefined {
  const picturesStore = usePicturesStore();

  return useMemo(() => selectPictureById(picturesStore, pictureId), [picturesStore, pictureId]);
}

export function usePictureDuplicates(pictureId: string): DuplicateMatch[] {
  const duplicateMatchesStore = useDuplicateMatchesStore();
  return useMemo(() => selectAllDuplicateMatches(duplicateMatchesStore).filter((m) => m.pictureId === pictureId), [
    duplicateMatchesStore,
    pictureId,
  ]);
}
