import { Tag } from '@types';
import { useSelector } from '@hooks';
import { selectAllTags } from '@reducers';

export function useTags(): Tag[] {
  return useSelector((state) => selectAllTags(state));
}
