import { useSelector } from '@hooks';
import { DuplicateMatchView, Picture } from '@types';

export function useActivePicture(): Picture | undefined {
  return useSelector((state) => state.pictures.activePicture.picture);
}

export function useActivePictureDuplicates(): DuplicateMatchView[] {
  return useSelector((state) => state.pictures.activePicture.duplicates);
}
