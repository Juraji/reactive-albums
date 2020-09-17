import { useSelector } from '@hooks';
import { DuplicateMatch, Picture } from '@types';

export function useActivePicture(): Picture | undefined {
  return useSelector((state) => state.pictures.activePicture.picture);
}

export function useActivePictureDuplicates(): DuplicateMatch[] {
  return useSelector((state) => state.pictures.activePicture.duplicates);
}
