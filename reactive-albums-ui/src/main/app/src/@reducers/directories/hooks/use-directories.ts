import { Directory } from '@types';
import { selectAllDirectories } from '@reducers';
import { useSelector } from '@hooks';

export function useDirectories(): Directory[] {
  return useSelector((state) => selectAllDirectories(state));
}
