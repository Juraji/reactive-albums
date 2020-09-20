import { useSelector } from '@hooks';
import { DuplicateMatch, Picture } from '@types';
import { selectActiveDuplicateMatches } from '../active-picture-slice';

export function useActivePicture(): Picture | undefined {
  return useSelector((state) => state.pictures.activePicture.picture || undefined);
}

export function useActivePictureDuplicates(): DuplicateMatch[] {
  return useSelector((state) => selectActiveDuplicateMatches(state));
}
