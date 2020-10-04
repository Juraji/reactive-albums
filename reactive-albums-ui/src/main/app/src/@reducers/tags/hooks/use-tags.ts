import { Tag } from '@types';
import { useSelector } from '@hooks';
import { selectAllColorTags, selectAllDirectoryTags, selectAllUserTags } from '../tag-store.slice';

export function useColorTags(): Tag[] {
  return useSelector((state) => selectAllColorTags(state));
}

export function useDirectoryTags(): Tag[] {
  return useSelector((state) => selectAllDirectoryTags(state));
}

export function useUserTags(): Tag[] {
  return useSelector((state) => selectAllUserTags(state));
}
